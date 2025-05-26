package com.rod.adenvi.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rod.adenvi.Adapters.CategoryListAdapter;
import com.rod.adenvi.Domains.CategoryListsDomain;
import com.rod.adenvi.R;

import java.util.ArrayList;


public class CategoryListFragment extends Fragment {

    RecyclerView recyclerview;
    LottieAnimationView progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment //
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        recyclerview = view.findViewById(R.id.recyclerview2);
        progressBar = view.findViewById(R.id.progressBar);

        // Retrieve Data //
        Retrieve();

        return view;
    }

    private void Retrieve() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String departmentName = sharedPreferences.getString("departmentName", "");
        String currentUsername = sharedPreferences.getString("currentusername", "");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);

        ArrayList<CategoryListsDomain> categoryListsDomains = new ArrayList<>();

        Log.d("CATEGORY", "Received username: " + currentUsername);

            DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference()
                    .child("inventory")
                    .child(currentUsername)
                    .child(departmentName);

            // Show the progress bar while retrieving data
            progressBar.setVisibility(View.VISIBLE);

            categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot itemsSnapshot : snapshot.getChildren()) {
                            String category = itemsSnapshot.getKey();

                            int itemvalues = 0;

                            for (DataSnapshot itemvaluesSnapshot : itemsSnapshot.getChildren()) {
                                itemvalues++;
                            }

                            CategoryListsDomain categoryListsDomain = new CategoryListsDomain(category, itemvalues);
                            categoryListsDomains.add(categoryListsDomain);
                        }

                        CategoryListAdapter categoryListsAdapter = new CategoryListAdapter(categoryListsDomains, departmentName, currentUsername);
                        recyclerview.setAdapter(categoryListsAdapter);

                        // Hide the progress bar when data retrieval is complete
                        progressBar.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }



}

