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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationSender {

    private static final String CHANNEL_ID = "channel_id";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    FirebaseUser user = mAuth.getInstance().getCurrentUser();
    private DatabaseReference showeringDataRef;

    public NotificationSender(DatabaseReference showeringDataRef) {
        this.showeringDataRef = showeringDataRef;
        this.db = db;
    }

    public void startListeningForNotifications(Context context) {
        showeringDataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                if (dataSnapshot.child("duration_minutes").getValue() != null
                        && dataSnapshot.child("temperature_mean").getValue() != null
                        && dataSnapshot.child("pressure_mean").getValue() != null) {
                    String key = dataSnapshot.getKey();
                    double waterUsedLiters = dataSnapshot.child("pressure_mean").getValue(Double.class);
                    double temperatureCelsius = dataSnapshot.child("temperature_mean").getValue(Double.class);
                    double durationMinutes = dataSnapshot.child("duration_minutes").getValue(Double.class);

                    String message = "Water Used: " + waterUsedLiters + " liters, Temperature: " + temperatureCelsius + "°C, Duration: " + durationMinutes + " minutes";
                    String notificationHistoryMessage =  "Water Used: " + waterUsedLiters + " liters\n" +
                            "Temperature: " + temperatureCelsius + "°C\n" +
                            "Duration: " + durationMinutes + " minutes\n";

                    sendNotification(context, message);
                    saveNotificationToFirestore(key, notificationHistoryMessage);
                    updateShowerData(waterUsedLiters, temperatureCelsius);
                } else {
                    Log.e("NotificationSender", "One of the values is null");
                }
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

        Intent intent = new Intent(context, NotificationsFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.nav_notificationmessage)
                .setContentTitle("New shower data available!")
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

    private void saveNotificationToFirestore(String key, String message) {
        String username = user.getEmail();
        Map<String, Object> notification = new HashMap<>();
        notification.put(key, (message + "Date: " + new Date()));

        // Create a reference to the document in the notificationsHistory collection
        DocumentReference docRef = db.collection("notificationsHistory").document(username);

        // Add field to document
        docRef.update(notification)
                .addOnSuccessListener(documentReference -> Log.d("NotificationSender", "Notification added to Firestore"))
                .addOnFailureListener(e -> Log.e("NotificationSender", "Error adding notification to Firestore", e));
    }

    private void updateShowerData(double waterUsedLiters, double temperatureCelcius) {
        String username = user.getEmail();
        DocumentReference docRef = db.collection("users").document(username);

        docRef.update("total sessions", FieldValue.increment(1))
                .addOnSuccessListener(documentReference -> Log.d("NotificationSender", "Notification added to Firestore"))
                .addOnFailureListener(e -> Log.e("NotificationSender", "Error adding notification to Firestore", e));

        docRef.update("total water used", FieldValue.increment(waterUsedLiters))
                .addOnSuccessListener(documentReference -> Log.d("NotificationSender", "Notification added to Firestore"))
                .addOnFailureListener(e -> Log.e("NotificationSender", "Error adding notification to Firestore", e));


        docRef.update("total temperature", FieldValue.increment(waterUsedLiters))
                .addOnSuccessListener(documentReference -> Log.d("NotificationSender", "Notification added to Firestore"))
                .addOnFailureListener(e -> Log.e("NotificationSender", "Error adding notification to Firestore", e));


    }


}
