package ordenese.vendor.common;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

import androidx.multidex.MultiDex;

import com.onesignal.OneSignal;

import ordenese.vendor.NotificationOpenedHandler;
import ordenese.vendor.R;
import ordenese.vendor.SunmiPrinterSDK.utils.SunmiPrintHelper;

public class ApplicationContext extends Application {

    private static ApplicationContext sInstance;

    public static ApplicationContext getInstance() {
        return sInstance;
    }

    private static final String ONESIGNAL_APP_ID = "bc344476-b51e-4e0c-9b8f-a8291c0f76d5";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.setNotificationOpenedHandler(new NotificationOpenedHandler(sInstance));
        OneSignal.unsubscribeWhenNotificationsAreDisabled(false);
//        OneSignal.provideUserConsent(true);
        OneSignal.initWithContext(this);

        SunmiPrintHelper.getInstance().initSunmiPrinterService(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppLanguageSupport.onAttach(base, "en"));
        MultiDex.install(this);
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

}
