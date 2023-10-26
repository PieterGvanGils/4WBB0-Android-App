package com.example.myapplication2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    private CardView analyticsCard;
    private CardView competitionCard;
    private CardView notificationsCard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize CardViews
        analyticsCard = view.findViewById(R.id.analyticsCard);
        competitionCard = view.findViewById(R.id.competitionCard);
        notificationsCard = view.findViewById(R.id.notificationsCard);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_container_view);

        // Set click listeners for CardViews
        analyticsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use NavController to navigate to the AnalyticsFragment
                navController.navigate(R.id.action_homeFragment_to_barChartFragment);
            }
        });

        competitionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use NavController to navigate to the CompetitionFragment
                navController.navigate(R.id.action_home_to_competition);
            }
        });

        notificationsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Use NavController to navigate to the NotificationsFragment
                navController.navigate(R.id.action_home_to_notifications);
            }
        });
    }

}