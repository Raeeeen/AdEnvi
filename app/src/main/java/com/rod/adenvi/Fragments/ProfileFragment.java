package com.rod.adenvi.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rod.adenvi.R;

public class ProfileFragment extends Fragment {

    TextView personName;
    CardView cardView;
    LottieAnimationView progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment //
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // View References //
        personName = view.findViewById(R.id.personName);
        progressBar = view.findViewById(R.id.progressBar);
        cardView = view.findViewById(R.id.cardView);

        // Hide the CardView and show the progress bar
        cardView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Retrieve Data from Firebase Database //
        Retrieve();

        return view;
    }

    // Retrieve Data from Firebase Database //
    public void Retrieve() {

        // Get the current user //
        String currentUsername = getArguments().getString("currentUsername");

        Log.d("ProfileFragment", "Received username: " + currentUsername);

            // Database Path Reference //
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUsername);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String personNameTxt = snapshot.child("fullName").getValue(String.class);

                    personName.setText(personNameTxt);
                    cardView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Hide the CardView and show a toast message if the retrieval fails
                    cardView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

    }


}