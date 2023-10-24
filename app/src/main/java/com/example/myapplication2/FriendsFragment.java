package com.example.myapplication2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.WriteResult;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private EditText editTextFriendGroupId;
    private Button btnCreateFriendGroup;
    private TextInputLayout dropdownMenu;
    private AutoCompleteTextView autoCompleteTextView;
    private EditText editTextFriendName;
    private Button btnAddFriend;
    private ListView listViewFriends;

    private List<String> friends; // List to store friends
    private List<String> friendGroups; // List to store friend groups
    private ArrayAdapter<String> friendGroupsAdapter;

    // private FirebaseFirestore db;
//    private CollectionReference friendGroupsRef;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference friendGroupsRef= db.collection("friendGroups");

    FirebaseAuth mAuth;
    // Initialize current user
    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // Initialize UI elements
        editTextFriendGroupId = view.findViewById(R.id.editTextFriendGroupId);
        btnCreateFriendGroup = view.findViewById(R.id.btn_CreateFriendGroup);
        dropdownMenu = view.findViewById(R.id.dropdown_menu);
        autoCompleteTextView = view.findViewById(R.id.auto_complete_txt);
        editTextFriendName = view.findViewById(R.id.editTextFriendName);
        btnAddFriend = view.findViewById(R.id.btn_AddFriend);
        listViewFriends = view.findViewById(R.id.listViewFriends);

//        // Initialize Firebase Firestore
//        db = FirebaseFirestore.getInstance();
//        friendGroupsRef = db.collection("friendGroups");

        // Initialize lists
        friends = new ArrayList<>();
        friendGroups = new ArrayList<>();
        friendGroupsAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, friendGroups);
        autoCompleteTextView.setAdapter(friendGroupsAdapter);

        // load all friends in listview
        loadFriends();

        // Set click listener for the "Create Friend Group" button
        btnCreateFriendGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendGroupId = editTextFriendGroupId.getText().toString().trim();
                if (!friendGroupId.isEmpty()) {
                    refreshFriendList();
                    // Add the friend group to Firestore with a custom ID
                    DocumentReference newFriendGroupRef = friendGroupsRef.document(friendGroupId);
                    FriendGroup newFriendGroup = new FriendGroup(friendGroupId);
                    newFriendGroup.addMember(String.valueOf(user.getEmail()));
                    newFriendGroupRef.set(newFriendGroup)
                            .addOnSuccessListener(aVoid -> {
                                friendGroups.add(friendGroupId);
                                friendGroupsAdapter.notifyDataSetChanged();
                                editTextFriendGroupId.setText("");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(),
                                        "Failed to create friend group: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        // Set click listener for the "Add Friend" button
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendEmail = editTextFriendName.getText().toString().trim();
                String selectedFriendGroup = autoCompleteTextView.getText().toString();
                refreshFriendList();

                if (!friendEmail.isEmpty() && !selectedFriendGroup.isEmpty()) {
                    // Create a reference to the document in the friendGroups collection
                    DocumentReference docRef = friendGroupsRef.document(selectedFriendGroup);

                    // Update the document
                    docRef.update("members", FieldValue.arrayUnion(friendEmail))
                            .addOnSuccessListener(aVoid -> {
                                friends.add(friendEmail);
                                editTextFriendName.setText("");
                                autoCompleteTextView.setText("");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(),
                                        "Failed to add friend: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                    friendGroupsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(),
                            "Please enter a friend name and select a friend group",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load friend groups from Firestore
        loadFriendGroups();

        // TODO: delete friends in listview and reload again

        return view;
    }

    private void loadFriendGroups() {
        friendGroupsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (FriendGroup friendGroup : queryDocumentSnapshots.toObjects(FriendGroup.class)) {
                friendGroups.add(friendGroup.getName());
                //Log.d("loadFriendGroup", "Friend group: " + friendGroup.getName());
            }
            friendGroupsAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to load friend groups: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void refreshFriendList() {
        String selectedFriendGroup = autoCompleteTextView.getText().toString();
        if (!selectedFriendGroup.isEmpty()) {
            friendGroupsRef.document(selectedFriendGroup).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    FriendGroup friendGroup = documentSnapshot.toObject(FriendGroup.class);
                    List<String> memberIds = friendGroup.getMembers();
                    friends.clear();
                    for (String memberId : memberIds) {
                        if (memberId != null ) { // !memberId.equals(String.valueOf(user.getEmail()))
                            friends.add(memberId);
                        }
                    }
                    // TODO: make sure that not only the first item of 'friends' is displayed in the listview
                    ArrayAdapter<String> friendsAdapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_list_item_1, friends);
                    listViewFriends.setAdapter(friendsAdapter);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(),
                        "Failed to load friends: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Load all friends
    private void loadFriends() {
        friendGroupsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (FriendGroup friendGroup : queryDocumentSnapshots.toObjects(FriendGroup.class)) {
                //Log.d("FriendGroup", "Friend group value: " + friendGroup);
                    List<String> memberIds = friendGroup.getMembers();
                    for (String memberId : memberIds) {
                        if (memberId != null && !memberId.equals(String.valueOf(user.getEmail()))) {
                            //Log.d("memberID", "memberIDs: "+ memberId);
                            friends.add(memberId);
                        }
                    }
                    //Log.d("friends list", "friends list: " + friends);
                    ArrayAdapter<String> friendsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, friends);
                    listViewFriends.setAdapter(friendsAdapter);
                    //Log.d("friendsAdapter", "friends adapter: " + friendsAdapter);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(),
                        "Failed to load friends: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            });
        }
    }