package com.rod.adenvi.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.rod.adenvi.LoginActivity;
import com.rod.adenvi.R;

public class SettingsFragment extends Fragment {

    LinearLayout logoutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment //
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        logoutBtn = view.findViewById(R.id.logoutBtn);

        // Button Functions //
        ButtonFunctions();

        return view;
    }

    // Button Functions //
    public void ButtonFunctions() {
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToLogin();

            }
        });
    }

    private void navigateToLogin() {
        // Clear the stored username
        SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("usernametxt");
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(0,0);
        getActivity().finish();
    }


}