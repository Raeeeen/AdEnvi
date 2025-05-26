package com.rod.adenvi.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rod.adenvi.Adapters.ItemListAdapter;
import com.rod.adenvi.Domains.ItemsListDomain;
import com.rod.adenvi.R;

import java.util.ArrayList;


public class ItemListFragment extends Fragment {

    RecyclerView recyclerview;
    LottieAnimationView progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment //
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        recyclerview = view.findViewById(R.id.recyclerview3);
        progressBar = view.findViewById(R.id.progressBar);

        Retrieve();

        return view;
    }

    private void Retrieve() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String departmentName = sharedPreferences.getString("departmentName", "");

        SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
        String categoryName = sharedPreferences2.getString("categoryName", "");
        String currentUsername = sharedPreferences.getString("currentusername", "");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);

        ArrayList<ItemsListDomain> itemsListDomains = new ArrayList<>();

            String deptName = departmentName.toString();
            String catName = categoryName.toString();

            DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference()
                    .child("inventory")
                    .child(currentUsername)
                    .child(departmentName)
                    .child(categoryName);

            // Show the progress bar while retrieving data
            progressBar.setVisibility(View.VISIBLE);

            itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot itemsSnapshot : snapshot.getChildren()) {
                            String items = itemsSnapshot.getKey();
                            ItemsListDomain itemsListDomain = new ItemsListDomain(items);
                            itemsListDomains.add(itemsListDomain);
                        }

                        ItemListAdapter itemsListAdapter = new ItemListAdapter(itemsListDomains, deptName, catName, currentUsername);
                        recyclerview.setAdapter(itemsListAdapter);

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