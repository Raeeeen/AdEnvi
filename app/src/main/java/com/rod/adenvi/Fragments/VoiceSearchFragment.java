package com.rod.adenvi.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rod.adenvi.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class VoiceSearchFragment extends Fragment {

    ImageButton submitbtn, cancelbtn, categorylist, deptofficelist, getimg;
    ImageView qrcode;
    EditText itemName, itemNumber, modelNumber, dateAcquired, categoryinfo, deptoffice;
    LinearLayout generateQR;
    TextView voiceempty;
    ConstraintLayout constraintLayoutdetails;
    LottieAnimationView progressBar;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private TextToSpeech textToSpeech;
    private boolean isTTSInitialized = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment //
        View view = inflater.inflate(R.layout.fragment_voice_search, container, false);

        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    isTTSInitialized = true;

                } else {

                }
            }
        });

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
        voiceempty = view.findViewById(R.id.voiceempty);
        progressBar = view.findViewById(R.id.progressBar);

        // Voice Search
        VoiceSearch();

        return view;
    }

    private void VoiceSearch() {
        startSpeechToText();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String spokenText = result.get(0);

                    // Remove spaces and convert to lowercase
                    spokenText = spokenText.replaceAll("\\s", "").toLowerCase();

                    // Get the current user //
                    String currentUsername = getArguments().getString("currentUsername");

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("voice code").child(currentUsername).child(spokenText);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {


                                String itemNametext = snapshot.child("itemName").getValue(String.class);
                                String itemNumbertext = snapshot.child("itemNumber").getValue(String.class);
                                String modelNumbertext = snapshot.child("modelNumber").getValue(String.class);
                                String dateAcqtext = snapshot.child("dateAcquired").getValue(String.class);
                                String deptOfficetext = snapshot.child("itemDeptOffice").getValue(String.class);
                                String categorytext = snapshot.child("itemCategory").getValue(String.class);

                                constraintLayoutdetails.setVisibility(View.GONE);
                                voiceempty.setVisibility(View.GONE);
                                progressBar.setVisibility(View.VISIBLE);

                                itemName.setText(itemNametext);
                                itemNumber.setText(itemNumbertext);
                                modelNumber.setText(modelNumbertext);
                                dateAcquired.setText(dateAcqtext);
                                deptoffice.setText(deptOfficetext);
                                categoryinfo.setText(categorytext);

                                String currentUsername = getArguments().getString("currentUsername");

                                final long ONE_MEGABYTE = 1024 * 1024;

                                StorageReference qrcodeImageRef = FirebaseStorage.getInstance().getReference()
                                        .child("users")
                                        .child(currentUsername)
                                        .child(deptOfficetext)
                                        .child(categorytext)
                                        .child(itemNametext)
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
                                        .child(deptOfficetext)
                                        .child(categorytext)
                                        .child(itemNametext)
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
                                // Data doesn't exist for the spoken text
                                // Handle the case when the spoken text is not found in Firebase
                                constraintLayoutdetails.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Data not found for the spoken text", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

                } else {

                    progressBar.setVisibility(View.GONE);
                    voiceempty.setVisibility(View.GONE);
                }
            }
        }
    }

    private void startSpeechToText() {
        // Intent to start the speech recognition activity
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US"); // You can change the language as needed

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            // Handle the case where speech recognition is not supported on the device
            // You can show a message to the user indicating that speech recognition is not available.
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }


}
