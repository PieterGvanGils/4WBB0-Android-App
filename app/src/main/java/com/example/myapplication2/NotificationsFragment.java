package com.example.myapplication2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

public class NotificationsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser user = mAuth.getInstance().getCurrentUser();
    private RecyclerView recyclerView;
    private NotificationHistoryAdapter adapter;
    private HashMap<String, Object> notificationItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.notification_history_recycler_view);
        notificationItems = new HashMap<>();

        // Retrieve data from Firestore
        retrieveNotificationHistory();

        adapter = new NotificationHistoryAdapter(notificationItems);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void retrieveNotificationHistory() {
        db.collection("notificationsHistory").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (documentSnapshot.exists()) {
                    notificationItems.putAll(documentSnapshot.getData());
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("FirestoreData", "Error getting documents", e));
    }
}
