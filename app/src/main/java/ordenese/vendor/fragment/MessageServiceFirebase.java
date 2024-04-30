package ordenese.vendor.fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ordenese.vendor.R;
import ordenese.vendor.activity.Activity_Home;

public class MessageServiceFirebase extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        showNotification("here", "testing", null, 1);

    }

    public void showNotification(String title, String description, Bitmap bitmap, int id) {

        Log.e("showNotification: ","received" );
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, Activity_Home.class);
        resultIntent.putExtra("id", id);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.error_logo_3)
                .setContentTitle(title)
                .setContentText(description)
                .setTicker(getString(R.string.app_name))
                .setContentIntent(resultPendingIntent);

        if (bitmap != null) {
            NotificationCompat.BigPictureStyle bpStyle = new NotificationCompat.BigPictureStyle();
            bpStyle.bigPicture(bitmap);
            bpStyle.build();
            builder.setStyle(bpStyle);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence _name = "channel_name";
            String _description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, _name, importance);
            channel.setDescription(_description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(id, builder.build());
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(id, builder.build());
        }
    }
}
