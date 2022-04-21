package com.pmspot.plius.presentation.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.appsflyer.AppsFlyerLib
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.pmspot.plius.R
import com.pmspot.plius.data.ApiFactory
import com.pmspot.plius.databinding.FragmentGameLoadBinding
import com.pmspot.plius.presentation.GameViewModel
import com.pmspot.plius.presentation.MyApplication
import com.pmspot.plius.presentation.activity.WebViewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GameLoadFragment : Fragment() {
    lateinit var binding: FragmentGameLoadBinding

    private var isDef = false //получаю с api ответ, для органики показывать вебвью или нет

    private var mainLink: String? = null //полный линк для webView

    //собираю обязательные данные с SDK
    private var cloacaLink: String? = null
    private var googleId: String? = null
    var appsFlyerUserId: String? = null
    private val bundle = "com.pmspot.plius"
    private val facebookToken = "eb_fAN0QcmqNO5Dly_fdw2QHRnY"
    private val appsDevKey = "NHiG2w9MT2Y45KZuPnSHcD"
    private val facebookAppId by lazy { resources.getString(R.string.facebook_app_id) }
    private var subAll = listOf<String?>(null, null, null, null) //данные с deeplink и appsFlyer разпарсенные

    private var campaign: String? = null
    private var deepLink: String? = null

    //не обязательные данные полученные с appsFlyer
    private var mediaSource: String? = null
    private var afStatus: String? = null
    private var afChannel: String? = null
    private var isFirstLaunch: String? = null

    //проверка первый ли вход
    private val user by lazy { requireActivity().getSharedPreferences("hasVisited", Context.MODE_PRIVATE) }
    private val visited by lazy { user.getBoolean("hasVisited", true) }

    //сохраняю ссылку для вебВью
    private val link by lazy { requireActivity().getSharedPreferences("link", Context.MODE_PRIVATE) }
    private val haveLink by lazy { link.getString("link", "") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameLoadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(R.drawable.bask).into(binding.imBask)

            //проверяю, если включен интернет, запускаю всю логику, если нет, сообщаю что нужно включить
            if (checkForInternet(requireActivity())){
                startWork()
            } else {
                Toast.makeText(requireActivity(),"Need to turn on internet", Toast.LENGTH_LONG).show()
            }
    }


    /**
     * Проверяю включен ли интернет
     */
    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    /**
     * Запускаю инициализацию и открываю новое окно
     */
    private fun startWork(){
        //проверка, если первый раз установил приложение, собирает данные и определяет что открыть
        //если второй, открывает что было прежде открыто
        if (visited){
            lifecycleScope.launch(Dispatchers.IO) {
                getDataServer() //получаю даннные с сервера(клоаку)
                getGoogleID() // получаю googleId
                startInitialFb() //инициализирую фб и получаю deepLink
                lifecycleScope.launch(Dispatchers.Main) {
                    getAppsFlyerParams() //получаю данные с AppsFlyer(внутри формирую ссылку и запускаю следующий экран)
                }
            }
            user.edit().putBoolean("hasVisited", false).apply()
        }else {
            if(haveLink.isNullOrEmpty()){
                findNavController().navigate(R.id.action_gameLoadFragment_to_gameMenuFragment)
            }else {
                Intent(requireActivity(), WebViewActivity::class.java).apply {
                    putExtra("link", haveLink)
                    startActivity(this)
                }
            }
        }
    }

    /**
     * Инициализирую фейсбук
     */
    private fun startInitialFb() {
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()
        AppLinkData.fetchDeferredAppLinkData(
            activity
        ) {
            deepLink = it?.targetUri.toString()
            deepLink?.let { deepString ->
                val arrayDeepLink = deepString.split("//")
                subAll = arrayDeepLink[1].split("_")
            }
        }
    }

    /**
     * Получаю id google
     */
    private fun getGoogleID() {
        val googleId = AdvertisingIdClient.getAdvertisingIdInfo(requireContext())
        this.googleId = googleId.id.toString()
    }


    /**
     * Получаю параметры с AppsFlyer
     */
    private fun getAppsFlyerParams() {
        appsFlyerUserId = AppsFlyerLib.getInstance().getAppsFlyerUID(requireActivity())
        MyApplication.liveDataAppsFlyer.observe(requireActivity()) {
            for (inform in it) {
                when (inform.key) {
                    "af_status" -> {
                        afStatus = inform.value.toString()
                    }
                    "campaign" -> {
                        campaign = inform.value.toString()
                        campaign?.let { it1 -> subAll = it1.split("_") }
                    }
                    "media_source" -> {
                        mediaSource = inform.value.toString()
                    }
                    "is_first_launch" -> {
                        isFirstLaunch = inform.value.toString()
                    }
                    "af_channel" -> {
                        afChannel = inform.value.toString()
                    }
                }
            }
            nextScreen() //открываю или игру или вебвью
        }
    }

    /**
     * Собираю ссылку
     */
    private fun collectingLink() {
        mainLink = cloacaLink + "media_source=$mediaSource" + "&google_adid=$googleId" +
                "&af_userid=$appsFlyerUserId" + "&bundle=$bundle" + "&fb_at=$facebookToken" +
                "&dev_key=$appsDevKey" + "&app_id=$facebookAppId" + "&media_source=$mediaSource" +
                "&af_status=$afStatus" + "&af_channel=$afChannel" + "&campaign=$campaign" +
                "&is_first_launch=$isFirstLaunch" + "&sub1=${subAll[0]}" + "&sub2=${subAll[1]}" +
                "&sub3=${subAll[2]}" + "&sub4=${subAll[3]}"
    }

    /**
     * Получаю данные с сервера
     */
    private fun getDataServer() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                ApiFactory.create().getDataServer().users.forEach {
                    Log.d("test1", "$it")
                    isDef = it.isdef.toBoolean()
                    cloacaLink = it.linka.toString()
                }
            }catch (e: Exception){
                Log.d("errorGetData", "$e")
            }
        }
    }

    /**
     * Открываю или игру или вебвью
     */
    private fun nextScreen() {
        collectingLink() //формирую ссылку
        if (afStatus == null || !isDef && afStatus == "Organic" && subAll[1] == null )  {
            findNavController().navigate(R.id.action_gameLoadFragment_to_gameMenuFragment)
        }
        if (isDef && afStatus == "Organic" || subAll[1] != null) {
            Intent(requireActivity(), WebViewActivity::class.java).apply {
                link.edit().putString("link", "$mainLink").apply()
                putExtra("link", mainLink)
                startActivity(this)
            }
        }
    }

}