package com.rod.adenvi.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rod.adenvi.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ItemsInfoSaveImageFragment extends Fragment {

    TextView itemName, dateacq, depOffice, qrcodeText;
    ImageView qrcode3;
    LinearLayout saveImage;
    ConstraintLayout imageLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment //
        View view = inflater.inflate(R.layout.fragment_items_info_save_image, container, false);
        itemName = view.findViewById(R.id.itemname);
        dateacq = view.findViewById(R.id.dateacq);
        depOffice = view.findViewById(R.id.depOffice);
        qrcode3 = view.findViewById(R.id.qrcode3);
        saveImage = view.findViewById(R.id.saveImage);
        imageLayout = view.findViewById(R.id.imageLayout);
        qrcodeText = view.findViewById(R.id.qrcodeText);

        ButtonFunctions();
        return view;
    }

    private void ButtonFunctions() {
        // Retrieve the data and QR code image from the Bundle
        Bundle bundle = getArguments();

        String itemNameTxt = bundle.getString("itemName");
        String dateAcquiredTxt = bundle.getString("dateAcquired");
        String deptOfficeTxt = bundle.getString("deptOffice");
        byte[] qrcodeImageByteArray = bundle.getByteArray("qrcodeImage");

        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String voiceCode = preferences.getString("voiceCode", null);

        // Convert the byte array back to a Bitmap for the QR code image
        Bitmap qrcodeImage = BitmapFactory.decodeByteArray(qrcodeImageByteArray, 0, qrcodeImageByteArray.length);
        Drawable drawable = new BitmapDrawable(getResources(), qrcodeImage);

        itemName.setText(itemNameTxt);
        dateacq.setText(dateAcquiredTxt);
        depOffice.setText(deptOfficeTxt);
        qrcode3.setImageDrawable(drawable);
        qrcodeText.setText(voiceCode);

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageLayout.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(imageLayout.getDrawingCache());
                imageLayout.setDrawingCacheEnabled(false);

                String fileName = itemNameTxt + ".jpg";
                String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                File imageFile = new File(dirPath, fileName);


                try {
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();

                    // Update the gallery by sending a broadcast
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(imageFile);
                    mediaScanIntent.setData(contentUri);
                    requireContext().sendBroadcast(mediaScanIntent);

                    Toast.makeText(requireContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show();

                    // Create a new ItemListFragment instance //
                    HomeFragment homeFragment = new HomeFragment();
                    // Get the FragmentManager //
                    FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                    // Start a FragmentTransaction //
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    // Replace the current fragment with the ItemListFragment //
                    transaction.replace(R.id.fragment_container, homeFragment);
                    transaction.commit();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}