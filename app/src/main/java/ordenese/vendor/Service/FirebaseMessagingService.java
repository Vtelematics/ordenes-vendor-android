package ordenese.vendor.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import ordenese.vendor.R;
import ordenese.vendor.activity.Activity_Splash_Screen;

public class
FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    NotificationManager mNotificationManager;
    int random = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80]

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {

            // playing audio and vibration when user se request
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                r.setLooping(false);
            }
            // vibration
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {100, 300, 300, 300};
            v.vibrate(pattern, -1);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");

            Intent resultIntent = new Intent(this, Activity_Splash_Screen.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);

            builder.setContentTitle(remoteMessage.getNotification().getTitle());
            builder.setContentText(remoteMessage.getNotification().getBody());
            builder.setSmallIcon(R.drawable.error_logo_3);
            builder.setContentIntent(pendingIntent);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
            builder.setAutoCancel(true);
            builder.setPriority(Notification.PRIORITY_MAX);

            mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                String channelId = String.valueOf(random);
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
                builder.setChannelId(channelId);
            }

// notificationId is a unique int for each notification that you must define
            random += 1;
            mNotificationManager.notify(random, builder.build());

        }

    }
}


