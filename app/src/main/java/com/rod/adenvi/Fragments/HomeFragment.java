package com.rod.adenvi.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.SharedPreferences;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rod.adenvi.Adapters.HomeAdapter;
import com.rod.adenvi.Domains.ItemsDomain;
import com.rod.adenvi.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    TextView emptytext;
    RecyclerView recyclerview;
    LottieAnimationView progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment //
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        emptytext = view.findViewById(R.id.emptytext);
        recyclerview = view.findViewById(R.id.recyclerview);
        progressBar = view.findViewById(R.id.progressBar);

        Retrieve();

        return view;
    }

    private void Retrieve() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);

        ArrayList<ItemsDomain> itemsDomains = new ArrayList<>();

        // Get the current user //
        String currentUsername = getArguments().getString("currentUsername");
        Log.d("HOMEFRAGMENT", "Received username: " + currentUsername);
        // Use the correct variable (currentUsername) here

        DatabaseReference deptOfficeRef = FirebaseDatabase.getInstance().getReference()
                .child("inventory")
                .child(currentUsername);


        // Show the progress bar while retrieving data
            progressBar.setVisibility(View.VISIBLE);

            deptOfficeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot deptOfficeSnapshot : snapshot.getChildren()) {
                            String deptOffice = deptOfficeSnapshot.getKey();

                            int categoryCount = 0;

                            for (DataSnapshot categorySnapshot : deptOfficeSnapshot.getChildren()) {
                                categoryCount++;
                            }

                            ItemsDomain itemsDomain = new ItemsDomain(deptOffice, categoryCount);

                            itemsDomains.add(itemsDomain);
                        }

                        // Get the current user //
                        String currentUsername = getArguments().getString("currentUsername");

                        HomeAdapter adapter = new HomeAdapter(itemsDomains, currentUsername);
                        recyclerview.setAdapter(adapter);

                        // Hide the progress bar when data retrieval is complete
                        progressBar.setVisibility(View.GONE);

                        if (itemsDomains.isEmpty()) {
                            emptytext.setVisibility(View.VISIBLE);
                        } else {
                            emptytext.setVisibility(View.GONE);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        emptytext.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });

        }

}