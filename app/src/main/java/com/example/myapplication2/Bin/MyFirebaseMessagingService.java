package com.example.myapplication2.Bin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.myapplication2.AnalyticsFragment;
import com.example.myapplication2.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "channel_id";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if the message contains data payload
        if (remoteMessage.getData().size() > 0) {
            // Handle the data payload of the received message
            String message = remoteMessage.getData().get("message");
            sendNotification(this,message);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("onCreate", "onCreate is called");
        // Static Testing Message
        sendNotification(this, "This is a test notification from inside onCreate");
        Log.d("Notification", "Test Notification send");

        /// Get a reference to the 'showering_data' node
        DatabaseReference showeringDataRef = FirebaseDatabase.getInstance().getReference("showering_data");

        // Add a ChildEventListener to listen for new child additions
        showeringDataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("addChildEventListner", "Listener activated!");
                // to avoid input error 1st argument sendNotification
                // Get the context of the listener class
                Context context = MyFirebaseMessagingService.this;

                // Get the values of the newly added session
                String key = dataSnapshot.getKey();
                int waterUsedLiters = dataSnapshot.child("water_used_liters").getValue(Integer.class);
                int temperatureCelsius = dataSnapshot.child("temperature_celsius").getValue(Integer.class);
                int durationMinutes = dataSnapshot.child("duration_minutes").getValue(Integer.class);

                // Trigger the notification with the relevant data
                String message = "New showering session - Water Used: " + waterUsedLiters + " liters, Temperature: " + temperatureCelsius + "°C, Duration: " + durationMinutes + " minutes";
                sendNotification(context, message);

                Log.d("NewSession", "Key: " + key);
                Log.d("NewSession", "Water Used (Liters): " + waterUsedLiters);
                Log.d("NewSession", "Temperature (Celsius): " + temperatureCelsius);
                Log.d("NewSession", "Duration (Minutes): " + durationMinutes);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle changes to existing children
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Handle removed children
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle when children change position
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.w("FirebaseError", "showeringDataRef:onCancelled", databaseError.toException());
            }
        });
    }

    public void sendNotification(Context context, String message) {
        int notificationId = 1;

        Intent intent = new Intent(context, AnalyticsFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.nav_notificationmessage)
                .setContentTitle("Notification Title")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId, notificationBuilder.build());
    }


    // TODO: implement somewhere
//    @Override
//    protected void onStop() {
//        super.onStop();
//        showeringDataRef.removeEventListener(childEventListener);
//    }

}

