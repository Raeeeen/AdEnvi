package com.rod.adenvi.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.rod.adenvi.R;

public class ItemScannerFragment extends Fragment {

    ImageButton submitbtn, cancelbtn, categorylist, deptofficelist, getimg;
    ImageView qrcode;
    EditText itemName, itemNumber, modelNumber, dateAcquired, categoryinfo, deptoffice;
    LinearLayout generateQR;
    TextView scancancel, scanempty;
    ConstraintLayout constraintLayoutdetails;
    LottieAnimationView progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment //
        View view = inflater.inflate(R.layout.fragment_item_scanner, container, false);
        scancancel = view.findViewById(R.id.scancancel);
        constraintLayoutdetails = view.findViewById(R.id.constraintlayoutdetails);
        submitbtn = view.findViewById(R.id.submitbtn);
        cancelbtn = view.findViewById(R.id.cancelbtn);
        categorylist = view.findViewById(R.id.categorylist);
        deptofficelist = view.findViewById(R.id.deptOfficeList);
        getimg = view.findViewById(R.id.getimg);
        qrcode = view.findViewById(R.id.qrcode2);
        itemName = view.findViewById(R.id.itemName);
        itemNumber = view.findViewById(R.id.itemNumber);
        modelNumber = view.findViewById(R.id.modelNumber);
        dateAcquired = view.findViewById(R.id.dateAcquired);
        categoryinfo = view.findViewById(R.id.categoryinfo);
        deptoffice = view.findViewById(R.id.deptOffice);
        generateQR = view.findViewById(R.id.generateQr);
        scanempty = view.findViewById(R.id.scanempty);
        progressBar = view.findViewById(R.id.progressBar);

        // Automatically start the QR code scanner //
        startQRCodeScanner();

        return view;
    }


    // Open QR Code Scanner //
    private void startQRCodeScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    // QR Code Functions //
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                scancancel.setVisibility(View.GONE);
                scanempty.setVisibility(View.GONE);
                constraintLayoutdetails.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                String scanResult = result.getContents();

                // Split the scanResult using a comma as the delimiter
                String[] parts = scanResult.split(",");

                // Now, assign the parts to individual variables
                String itemname = parts[0];
                String itemnumber = parts[1];
                String modelNo = parts[2];
                String dateAcq = parts[3];
                String departmentOffice = parts[4];
                String category = parts[5];

                itemName.setText(itemname);
                itemNumber.setText(itemnumber);
                modelNumber.setText(modelNo);
                dateAcquired.setText(dateAcq);
                deptoffice.setText(departmentOffice);
                categoryinfo.setText(category);

                String currentUsername = getArguments().getString("currentUsername");

                final long ONE_MEGABYTE = 1024 * 1024;

                StorageReference qrcodeImageRef = FirebaseStorage.getInstance().getReference()
                        .child("users")
                        .child(currentUsername)
                        .child(departmentOffice)
                        .child(category)
                        .child(itemname)
                        .child(currentUsername + ".png");


                qrcodeImageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Successfully fetched the QR code image
                        Bitmap qrcodeBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        qrcode.setImageBitmap(qrcodeBitmap);
                        // Show the Items Layout(Constraint) and hide the progress bar when data retrieval is complete
                        constraintLayoutdetails.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Hide the Items Layout(Constraint) and show a toast message if the retrieval fails
                        constraintLayoutdetails.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to retrieve QR Code", Toast.LENGTH_SHORT).show();
                    }
                });

                StorageReference itemimgImageRef = FirebaseStorage.getInstance().getReference()
                        .child("users")
                        .child(currentUsername)
                        .child(departmentOffice)
                        .child(category)
                        .child(itemname)
                        .child(".png");

                itemimgImageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Successfully fetched the item image
                        Bitmap itemimgBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        getimg.setImageBitmap(itemimgBitmap);

                        // Show the Items Layout(Constraint) and hide the progress bar when data retrieval is complete
                        constraintLayoutdetails.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Hide the Items Layout(Constraint) and show a toast message if the retrieval fails
                        constraintLayoutdetails.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        // Handle the failure to fetch the item image
                        Toast.makeText(getContext(), "Failed to retrieve Item Image", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                progressBar.setVisibility(View.GONE);
                scanempty.setVisibility(View.GONE);
                scancancel.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Scan cancelled", Toast.LENGTH_SHORT).show();
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

}