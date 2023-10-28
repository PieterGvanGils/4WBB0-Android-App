package com.example.myapplication2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CompetitionFragment extends Fragment {

    private FirebaseUser user;
    private List<String> friendGroups;

    private AutoCompleteTextView autoCompleteTextViewCompetition;
    private RecyclerView recyclerView;
    private CompetitionAdapter competitionAdapter;
    private List<CompetitionItem> competitionItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        friendGroups = new ArrayList<>();
        competitionItems = new ArrayList<>();
        fetchFriendGroups();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_competition, container, false);
        autoCompleteTextViewCompetition = rootView.findViewById(R.id.auto_complete_txt_competition);

        // Setup the RecyclerView
        recyclerView = rootView.findViewById(R.id.competitionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter with an empty list
        competitionAdapter = new CompetitionAdapter(competitionItems);
        recyclerView.setAdapter(competitionAdapter);

        // Set up the listener for the AutoCompleteTextView
        autoCompleteTextViewCompetition.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedFriendGroup = (String) adapterView.getItemAtPosition(i);
            showRanking(selectedFriendGroup);
        });

        return rootView;
    }

    // Fetch the list of friend groups that the user is part of
    private void fetchFriendGroups() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("friendGroups")
                .whereArrayContains("members", user.getEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String groupName = document.getString("name");
                        friendGroups.add(groupName);
                    }
                    updateAutoCompleteTextView();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(),
                            "Failed to fetch friendgroup: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAutoCompleteTextView() {
        AutoCompleteTextView autoCompleteTextViewCompetition = requireView().findViewById(R.id.auto_complete_txt_competition);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, friendGroups);
        autoCompleteTextViewCompetition.setAdapter(adapter);
    }

    public void retrieveAverageWaterUsed(String username, OnCompleteListener<Double> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(username);
        docRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            int sessions = document.getLong("total sessions").intValue();
                            double totalWaterUsed = document.getDouble("total water used");
                            double averageWaterUsed = totalWaterUsed / sessions;
                            listener.onComplete(Tasks.forResult(averageWaterUsed));
                        } else {
                            Log.d("No such document!", "No such document exists for the username: " + username);
                            listener.onComplete(Tasks.forResult(0.0));
                        }
                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    public void showRanking(String selectedFriendGroup) {
        if (selectedFriendGroup == null || selectedFriendGroup.isEmpty()) {
            Log.d("showRanking", "Selected friend group is empty or null");
            return;
        }
        Map<String, Double> averagesMap = new HashMap<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection("friendGroups").document(selectedFriendGroup);

        documentRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> members = (List<String>) documentSnapshot.get("members");
                        AtomicInteger count = new AtomicInteger(0);

                        for (String member : members) {
                            try {
                                retrieveAverageWaterUsed(member, new OnCompleteListener<Double>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Double> task) {
                                        try {
                                            if (task.isSuccessful()) {
                                                double averageWaterUsed = task.getResult();
                                                averagesMap.put(member, averageWaterUsed);
                                                int currentCount = count.incrementAndGet();
                                                if (currentCount == members.size()) {
                                                    displayRankings(new ArrayList<>(averagesMap.keySet()), competitionItems);
                                                }
                                            } else {
                                                Log.d("Error", "Error retrieving average water used: " + task.getException());
                                            }
                                        } catch (Exception e) {
                                            Log.e("Exception", "Exception in onComplete: " + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.e("Exception", "Exception in try block: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.d("showRanking", "Document does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to retrieve friend group: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void displayRankings(List<String> members, List<CompetitionItem> competitionItems) {
        Map<String, Double> averagesMap = new HashMap<>();
        AtomicInteger completedTasks = new AtomicInteger(0);

        for (String member : members) {
            try {
                retrieveAverageWaterUsed(member, new OnCompleteListener<Double>() {
                    @Override
                    public void onComplete(@NonNull Task<Double> task) {
                        if (task.isSuccessful()) {
                            double averageWaterUsed = task.getResult();
                            averagesMap.put(member, averageWaterUsed);
                        } else {
                            Log.d("Error", "Error retrieving average water used: " + task.getException());
                        }

                        int count = completedTasks.incrementAndGet();
                        if (count == members.size()) {
                            List<Map.Entry<String, Double>> sortedAverages = new ArrayList<>(averagesMap.entrySet());
                            sortedAverages.sort(Map.Entry.comparingByValue());

                            for (int i = 0; i < sortedAverages.size(); i++) {
                                String username = sortedAverages.get(i).getKey();
                                double avgWaterUsed = sortedAverages.get(i).getValue();

                                // Rounding to two decimal places
                                DecimalFormat df = new DecimalFormat("#.##");
                                avgWaterUsed = Double.parseDouble(df.format(avgWaterUsed));

                                int rank = i + 1;
                                CompetitionItem item = new CompetitionItem(rank, username, avgWaterUsed);
                                competitionItems.add(item);
                            }
                            competitionAdapter.notifyDataSetChanged();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
