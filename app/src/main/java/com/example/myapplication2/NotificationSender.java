package com.example.myapplication2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class NotificationSender {

    private static final String CHANNEL_ID = "channel_id";

    private DatabaseReference showeringDataRef;

    public NotificationSender(DatabaseReference showeringDataRef) {
        this.showeringDataRef = showeringDataRef;
    }

    public void startListeningForNotifications(Context context) {
        showeringDataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("addChildEventListner", "Listener activated!");

                String key = dataSnapshot.getKey();
                int waterUsedLiters = dataSnapshot.child("water_used_liters").getValue(Integer.class);
                int temperatureCelsius = dataSnapshot.child("temperature_celsius").getValue(Integer.class);
                int durationMinutes = dataSnapshot.child("duration_minutes").getValue(Integer.class);

                String message = "New showering session - Water Used: " + waterUsedLiters + " liters, Temperature: " + temperatureCelsius + "°C, Duration: " + durationMinutes + " minutes";
                sendNotification(context, message);

                Log.d("NewSession", "Key: " + key);
                Log.d("NewSession", "Water Used (Liters): " + waterUsedLiters);
                Log.d("NewSession", "Temperature (Celsius): " + temperatureCelsius);
                Log.d("NewSession", "Duration (Minutes): " + durationMinutes);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(Context context, String message) {
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
}
