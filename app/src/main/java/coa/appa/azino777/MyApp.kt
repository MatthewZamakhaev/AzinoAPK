package coa.appa.azino777

import android.app.Application
import android.content.Context
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream


class MyApp : Application() {
    companion object {
        @JvmStatic var campaign: String? = null
        @JvmStatic var gaid: String? = null

        @JvmStatic
        fun fetchGAID(callback: (String) -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                while (gaid == null) {
                    delay(100)
                }
                withContext(Dispatchers.Main) {
                    callback(gaid ?: "unknown")
                }
            }
        }

        @JvmStatic
        fun getCampaignFromAssets(context: Context): String? {
            return try {
                val inputStream: InputStream = context.assets.open("config.json")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                val json = String(buffer, Charsets.UTF_8)
                JSONObject(json).getString("campaign")
            } catch (ex: Exception) {
                Log.e("MyApp", "Ошибка чтения кампании", ex)
                null
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
                gaid = adInfo.id
                Log.d("GAID", "GAID загружен: $gaid")
            } catch (e: Exception) {
                Log.e("GAID", "Ошибка получения GAID", e)
                gaid = "unknown"
            }
        }

        OneSignal.initWithContext(this);
        OneSignal.setAppId("d90c89ea-ac16-4c7e-8c70-53fb9052a36a");
        val afDevKey = "fVjUwR8kk6ABDEgfhwUSqh"
        val appsflyer = AppsFlyerLib.getInstance()
        appsflyer.setDebugLog(true)
        appsflyer.setMinTimeBetweenSessions(0)
        appsflyer.setOutOfStore("Azino")

        val conversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionDataMap: MutableMap<String, Any>) {
                Log.d("AppsFlyer", "Conversion Data: $conversionDataMap")
            }

            override fun onConversionDataFail(errorMessage: String) {
                Log.d("AppsFlyer", "Error getting conversion data: $errorMessage")
            }

            override fun onAppOpenAttribution(attributionData: Map<String, String>) {
                Log.d("AppsFlyer", "onAppOpenAttribution: This is a fake call.")
            }

            override fun onAttributionFailure(errorMessage: String) {
                Log.d("AppsFlyer", "Error onAttributionFailure: $errorMessage")
            }
        }
        appsflyer.init(afDevKey, conversionListener, this)
        appsflyer.start(this)

        campaign = getCampaignFromAssets(this)
        manuallySendInstallToAppsFlyer(campaign!!)
        Log.d("MyApp", "Кампания загружена: $campaign")
    }

    private fun manuallySendInstallToAppsFlyer(campaign: String) {
        val installData: MutableMap<String, Any?> = HashMap()
        installData["pid"] = "apk_install" // Указываем источник установки
        installData["c"] = campaign // Кампания
        installData["af_channel"] = "direct_apk" // Канал установки
        installData["af_status"] = "Non-organic" // Отмечаем как рекламную установку

        // Устанавливаем AppsFlyer UID
        val afUid = AppsFlyerLib.getInstance().getAppsFlyerUID(this)
        installData["af_install_id"] = afUid

        // Указываем, что APK установлен вне стора
        AppsFlyerLib.getInstance().setOutOfStore("Azino")

        // Логируем установку
        AppsFlyerLib.getInstance().logEvent(this, "af_first_open", installData)

        Log.d("AppsFlyer", "Ручная установка отправлена с кампанией: $campaign")
    }
}