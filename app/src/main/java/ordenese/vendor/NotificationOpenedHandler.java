package ordenese.vendor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.math.BigInteger;

import ordenese.vendor.activity.Activity_BackBtn_Container;
import ordenese.vendor.activity.Activity_Home;
import ordenese.vendor.common.ApplicationContext;
import ordenese.vendor.common.Constant;

public class NotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler, OneSignal.OSRemoteNotificationReceivedHandler {

    ApplicationContext mInstance;

    public NotificationOpenedHandler(ApplicationContext mInstance) {
        this.mInstance = mInstance;
    }

    @Override
    public void notificationOpened(OSNotificationOpenedResult osNotificationOpenedResult) {
        start_Activity();
    }

    private void start_Activity() {
        try {
            if (!Constant.DataGetValue(mInstance, Constant.Token).equals("empty")) {
                Intent intent = new Intent(mInstance, Activity_Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                mInstance.startActivity(intent);
            } else {
                Intent intent = new Intent(mInstance, Activity_BackBtn_Container.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                mInstance.startActivity(intent);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent osNotificationReceivedEvent) {
        OSNotification notification = osNotificationReceivedEvent.getNotification();

        // Example of modifying the notification's accent color
        OSMutableNotification mutableNotification = notification.mutableCopy();
        mutableNotification.setExtender(builder -> {
            // Sets the accent color to Green on Android 5+ devices.
            // Accent color controls icon and action buttons on Android 5+. Accent color does not change app title on Android 10+
            builder.setColor(new BigInteger("FF00FF00", 16).intValue());
            // Sets the notification Title to Red
            Spannable spannableTitle = new SpannableString(notification.getTitle());
            spannableTitle.setSpan(new ForegroundColorSpan(Color.RED), 0, notification.getTitle().length(), 0);
            builder.setContentTitle(spannableTitle);
            // Sets the notification Body to Blue
            Spannable spannableBody = new SpannableString(notification.getBody());
            spannableBody.setSpan(new ForegroundColorSpan(Color.BLUE), 0, notification.getBody().length(), 0);
            builder.setContentText(spannableBody);
            //Force remove push from Notification Center after 30 seconds
            builder.setTimeoutAfter(60000);
            return builder;
        });
        JSONObject data = notification.getAdditionalData();
        Log.i("OneSignalExample", "Received Notification Data: " + data);

        // If complete isn't call within a time period of 25 seconds, OneSignal internal logic will show the original notification
        // To omit displaying a notification, pass `null` to complete()
        osNotificationReceivedEvent.complete(mutableNotification);
    }
}

