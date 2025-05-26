package com.rod.adenvi.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rod.adenvi.R;

import java.io.ByteArrayOutputStream;

public class ItemInfoFragment extends Fragment {

    ImageButton submitbtn, cancelbtn, categorylist, deptofficelist, getImage, imtag;
    ImageView qrcode;
    EditText itemName, itemNumber, modelNumber, dateAcquired, categoryinfo, deptoffice;
    LinearLayout generateQR;
    LottieAnimationView progressBar;
    ConstraintLayout itemsinfoLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment //
        View view = inflater.inflate(R.layout.fragment_item_info, container, false);
        submitbtn = view.findViewById(R.id.submitbtn);
        cancelbtn = view.findViewById(R.id.cancelbtn);
        categorylist = view.findViewById(R.id.categorylist);
        deptofficelist = view.findViewById(R.id.deptOfficeList);
        getImage = view.findViewById(R.id.getImage);
        imtag = view.findViewById(R.id.imtag);
        qrcode = view.findViewById(R.id.qrcode2);
        itemName = view.findViewById(R.id.itemName);
        itemNumber = view.findViewById(R.id.itemNumber);
        modelNumber = view.findViewById(R.id.modelNumber);
        dateAcquired = view.findViewById(R.id.dateAcquired);
        categoryinfo = view.findViewById(R.id.categoryinfo);
        deptoffice = view.findViewById(R.id.deptOffice);
        generateQR = view.findViewById(R.id.generateQr);
        progressBar = view.findViewById(R.id.progressBar);
        itemsinfoLayout = view.findViewById(R.id.itemsinfoLayout);

        // Hide the Items Info Layout(Constraint) and show the progress bar
        itemsinfoLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Retrieve();
        ButtonFunctions();

        return view;
    }

    private void Retrieve() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String departmentName = sharedPreferences.getString("departmentName", "");

        SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
        String categoryName = sharedPreferences2.getString("categoryName", "");

        SharedPreferences sharedPreferences3 = requireContext().getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE);
        String itemsName = sharedPreferences3.getString("itemsName", "");
        String currentUsername = sharedPreferences.getString("currentusername", "");


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("inventory")
                .child(currentUsername)
                .child(departmentName)
                .child(categoryName)
                .child(itemsName);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String itemNameText = itemsName;
                String itemNumberText = snapshot.child("itemNumber").getValue(String.class);
                String modelNumberText = snapshot.child("modelNumber").getValue(String.class);
                String dateAcquiredText = snapshot.child("dateAcquired").getValue(String.class);
                String deptOfficeText = departmentName;
                String categoryText = categoryName;

                itemName.setText(itemNameText);
                itemNumber.setText(itemNumberText);
                modelNumber.setText(modelNumberText);
                deptoffice.setText(deptOfficeText);
                dateAcquired.setText(dateAcquiredText);
                categoryinfo.setText(categoryText);

                SharedPreferences sharedPreferences3 = requireContext().getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE);
                String currentUsername = sharedPreferences3.getString("currentusername", "");

                StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                        .child("users")
                        .child(currentUsername)
                        .child(departmentName)
                        .child(categoryName)
                        .child(itemsName)
                        .child(currentUsername + ".png");

                StorageReference storageReference1 = FirebaseStorage.getInstance().getReference()
                        .child("users")
                        .child(currentUsername)
                        .child(departmentName)
                        .child(categoryName)
                        .child(itemsName)
                        .child(".png");

                final long ONE_MEGABYTE = 1024 * 1024;

                storageReference1.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        getImage.setImageBitmap(bitmap);

                        // Show the Items Layout(Constraint) and hide the progress bar when data retrieval is complete
                        itemsinfoLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Hide the Items Layout(Constraint) and show a toast message if the retrieval fails
                        itemsinfoLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to retrieve image", Toast.LENGTH_SHORT).show();
                    }
                });

                storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        qrcode.setImageBitmap(bitmap);

                        // Show the Items Layout(Constraint) and hide the progress bar when data retrieval is complete
                        itemsinfoLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Hide the Items Layout(Constraint) and show a toast message if the retrieval fails
                        itemsinfoLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to retrieve QR code", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Hide the Items Layout(Constraint) and show a toast message if the retrieval fails
                itemsinfoLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void ButtonFunctions() {

        imtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a Bundle to pass data to the other fragment
                Bundle bundle = new Bundle();
                bundle.putString("itemName", itemName.getText().toString());
                bundle.putString("dateAcquired", dateAcquired.getText().toString());
                bundle.putString("deptOffice", deptoffice.getText().toString());

                // Convert the qrcode Bitmap to a byte array
                Bitmap qrcodeBitmap = ((BitmapDrawable) qrcode.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                qrcodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bundle.putByteArray("qrcodeImage", byteArray);

                // Create an instance of the other fragment where you want to pass the data
                ItemsInfoSaveImageFragment itemsInfoSaveImageFragment = new ItemsInfoSaveImageFragment();
                itemsInfoSaveImageFragment.setArguments(bundle);

                // Use FragmentManager to replace the current fragment with the other fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, itemsInfoSaveImageFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the FragmentManager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                // Pop the current fragment from the back stack
                fragmentManager.popBackStack();
            }
        });


    }


}