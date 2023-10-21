package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.myapplication2.HomeFragment;
import com.example.myapplication2.SettingsFragment;
import com.example.myapplication2.AnalyticsFragment;
import com.example.myapplication2.FriendsFragment;
import com.example.myapplication2.NotificationsFragment;
import com.example.myapplication2.CompetitionFragment;
import com.example.myapplication2.LogoutFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;
    private NavController.OnDestinationChangedListener listener;

    private DatabaseReference showeringDataRef;

    // Stevdza-San Tutorial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_view);
        NavController navController = navHostFragment.getNavController();
        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //setup Firestore Database
        FirebaseApp.initializeApp(this);

        // setup firebase for notifications
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        showeringDataRef = database.getReference("showering_data");
        NotificationSender notificationSender = new NotificationSender(showeringDataRef);
        notificationSender.startListeningForNotifications(this);

//        if (savedInstanceState == null) {
//            Log.d("SIS", "savedInstance is null");
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new HomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
//        }
    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new HomeFragment()).commit();

        } else if (itemId == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new SettingsFragment()).commit();

        } else if (itemId == R.id.nav_analytics) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new AnalyticsFragment()).commit();

        } else if (itemId == R.id.nav_competition) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new CompetitionFragment()).commit();

        } else if (itemId == R.id.nav_friends) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new FriendsFragment()).commit();

        } else if (itemId == R.id.nav_logout) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new LogoutFragment()).commit();

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_view);
        NavController navController = navHostFragment.getNavController();
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        if (!navController.navigateUp()) {
//            super.onBackPressed();
//        }
    }
}
