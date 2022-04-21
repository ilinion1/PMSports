package com.pmspot.plius.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.onesignal.OneSignal

class MyApplication: Application() {

    private val oneSignalKey = "fee2126f-9bf6-4f98-9bad-8f789052ab9c"
    private val keyDevAppsflyer = "NHiG2w9MT2Y45KZuPnSHcD"
    private var getData = false
    companion object{
        var liveDataAppsFlyer = MutableLiveData<MutableMap<String, Any>>()
    }

    override fun onCreate() {
        super.onCreate()
        AppsFlyerLib.getInstance().init(keyDevAppsflyer, appsFlyerConversion(), this)
        AppsFlyerLib.getInstance().start(this)
        OneSignal.initWithContext(this);
        OneSignal.setAppId(oneSignalKey)
    }

    private fun appsFlyerConversion(): AppsFlyerConversionListener {

        return object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                if (!getData){
                    data?.let {
                        liveDataAppsFlyer.postValue(it)
                    }
                    getData = true
                }
            }

            override fun onConversionDataFail(error: String?) {
                Log.d("error", "$error")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                Log.d("data", "$data")
            }

            override fun onAttributionFailure(error: String?) {
                Log.d("error", "$error")
            }
        }
    }
}