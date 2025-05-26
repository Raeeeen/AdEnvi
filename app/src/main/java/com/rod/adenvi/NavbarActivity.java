package com.rod.adenvi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.rod.adenvi.Fragments.AddItemFragment;
import com.rod.adenvi.Fragments.HomeFragment;
import com.rod.adenvi.Fragments.ItemInfoFragment;
import com.rod.adenvi.Fragments.ItemScannerFragment;
import com.rod.adenvi.Fragments.ProfileFragment;
import com.rod.adenvi.Fragments.SettingsFragment;
import com.rod.adenvi.Fragments.VoiceSearchFragment;

public class NavbarActivity extends AppCompatActivity {

    RelativeLayout fragment_container;
    ChipNavigationBar chipNavigationBar;
    ChangeBounds changeBounds = new ChangeBounds();
    float x1, x2;
    static final int MIN_DISTANCE = 150;
    private boolean isButtonClickable = true;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FullScreen //
        FullScreen();

        // View References //
        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        fragment_container = findViewById(R.id.fragment_container);

        // Set the selected item and load the initial fragment //
        chipNavigationBar.setItemSelected(R.id.bottom_nav_home, true);

        // Get the current user login //
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUsername = preferences.getString("usernametxt", "default_value_if_not_found");
        Log.d("NAVBAR", "Received username: " + currentUsername);

        // Initialize HomeFragment with the current username //
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("currentUsername", currentUsername);
        homeFragment.setArguments(bundle);

        // Load the initial fragment (HomeFragment) with the bundle //
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        // Navbar Functions //
        NavbarFunctions();
    }

    // FullScreen //
    private void FullScreen() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.navbar_activity);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    // NavbarFunctions //
    private void NavbarFunctions() {

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;

                // Create a bundle and set the current username
                Bundle bundle = new Bundle();
                bundle.putString("currentUsername", currentUsername);

                if (i == R.id.bottom_nav_home) {
                    fragment = new HomeFragment();
                } else if (i == R.id.bottom_nav_profile) {
                    fragment = new ProfileFragment();
                } else if (i == R.id.bottom_nav_settings) {
                    fragment = new SettingsFragment();
                } else if (i == R.id.bottom_nav_additem) {
                    fragment = new AddItemFragment();
                } else if (i == R.id.voice_search) {
                    if(isButtonClickable) {
                        fragment = new VoiceSearchFragment();

                        // Disable the button
                        isButtonClickable = false;

                        // Set a delay of 2 seconds
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Enable button after the delay
                                isButtonClickable = true;
                            }
                        }, 2000); // 2000 = 2sec
                    }

                } else if (i == R.id.bottom_nav_itemscanner) {
                    if (isButtonClickable) {
                        fragment = new ItemScannerFragment();

                        // Disable the button
                        isButtonClickable = false;

                        // Set a delay of 2 seconds
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Enable the button after the delay
                                isButtonClickable = true;
                            }
                        }, 2000); // 2000 milliseconds = 2 seconds
                    }
                }

                // Set the bundle as arguments for each fragment
                if (fragment != null) {
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            }
        });

        chipNavigationBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;

                        // Check for left swipe (collapse)
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (x2 > x1 && !chipNavigationBar.isExpanded()) {
                                // Swipe right, expand
                                TransitionManager.beginDelayedTransition(fragment_container, changeBounds);
                                chipNavigationBar.expand();
                            } else if (x2 < x1 && chipNavigationBar.isExpanded()) {
                                // Swipe left, collapse
                                TransitionManager.beginDelayedTransition(fragment_container, changeBounds);
                                chipNavigationBar.collapse();
                            }
                        }

                        break;
                }
                return true;
            }
        });

    }





}
