package ordenese.vendor.common;

import android.content.Context;
import android.os.Handler;

import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONObject;


public class NotificationServiceExtension implements OneSignal.OSRemoteNotificationReceivedHandler {

    private Handler handlerDriverAssign, handlerDirectDriverAssign;
    private Runnable runnableDriverAssign, runnableDirectDriverAssign;

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent osNotificationReceivedEvent) {

        OSNotification notification = osNotificationReceivedEvent.getNotification();

        JSONObject data = notification.getAdditionalData();
        String body = notification.getBody();

        //Test
//        Log.e("Test","Additional Data="+data);
//        Log.e("Test","Body="+body);

        // Example of modifying the notification's accent color
        OSMutableNotification mutableNotification = notification.mutableCopy();

        //JSONObject data = notification.getAdditionalData();
//        Log.e("Test", "NotificationReceivedIn Background ");
//
//        Log.e("Test", "NotificationReceivedIn AdditionalData " + notification.getAdditionalData());
//        Log.e("Test", "NotificationReceivedIn Body" + notification.getBody());
//        Log.e("Test", "NotificationReceivedIn Sound" + notification.getSound());
//        Log.e("Test", "NotificationReceivedIn Title" + notification.getTitle());


        // If complete isn't call within a time period of 25 seconds, OneSignal internal logic will show the original notification
        // To omit displaying a notification, pass `null` to complete()
        osNotificationReceivedEvent.complete(mutableNotification);


        //There is an order near you, Order ID:1636. Can you deliver?
        //You have been assiged a delivery!


//There is an order near you, Order ID:1636. Can you deliver?
        //You have been assiged a delivery!

        //notification: com.onesignal.OSNotification@f85bbbb
        // notification.payload.toString: com.onesignal.OSNotificationPayload@8c618d8
        // notification.payload.body: There is an order near you, Order ID:1632. Can you deliver?
        //data: {"delivery_address":"Vadavalli Bus Stand, Coimbatore, Tamil Nadu 641041, India","delivery_id":"1632","pickup_address":"Coimbatore, Tamil Nadu 641041, India","payment_method":"Gotówką przy odbiorze"}
        //notification.toJSONObject(): {"isAppInFocus":true,"shown":true,"androidNotificationId":-1384837070,"displayType":0,"payload":{"notificationID":"58d348a7-0be6-4e61-8910-d41e62551f01","body":"There is an order near you, Order ID:1631. Can you deliver?","additionalData":{"delivery_address":"Vadavalli Bus Stand, Coimbatore, Tamil Nadu 641041, India","delivery_id":"1631","pickup_address":"Coimbatore, Tamil Nadu 641041, India","payment_method":"Gotówką przy odbiorze"},"lockScreenVisibility":1,"fromProjectNumber":"667245792395","priority":0,"rawPayload":"{\"google.delivered_priority\":\"normal\",\"google.sent_time\":1651053219891,\"google.ttl\":259200,\"google.original_priority\":\"normal\",\"custom\":\"{\\\"a\\\":{\\\"delivery_address\\\":\\\"Vadavalli Bus Stand, Coimbatore, Tamil Nadu 641041, India\\\",\\\"delivery_id\\\":\\\"1631\\\",\\\"pickup_address\\\":\\\"Coimbatore, Tamil Nadu 641041, India\\\",\\\"payment_method\\\":\\\"Gotówką przy odbiorze\\\"},\\\"i\\\":\\\"58d348a7-0be6-4e61-8910-d41e62551f01\\\"}\",\"from\":\"667245792395\",\"alert\":\"There is an order near you, Order ID:1631. Can you deliver?\",\"google.message_id\":\"0:1651053219904610%e6d324edf9fd7ecd\",\"google.c.sender.id\":\"667245792395\",\"androidNotificationId\":-1384837070}"}}

        /*{
	      "delivery_address": "Vadavalli Bus Stand, Coimbatore, Tamil Nadu 641041, India",
	      "delivery_id": "1632",
	      "pickup_address": "Coimbatore, Tamil Nadu 641041, India",
	      "payment_method": "Gotówką przy odbiorze"
       }*/

    }

}
