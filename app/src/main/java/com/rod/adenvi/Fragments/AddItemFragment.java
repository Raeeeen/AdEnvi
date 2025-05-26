package com.rod.adenvi.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.rod.adenvi.R;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class AddItemFragment extends Fragment {

    ImageButton submitbtn, cancelbtn, categorylist, deptofficelist, additemimg;
    LinearLayout generateQR;
    ImageView qrcode;
    EditText itemName, itemNumber, modelNumber, dateAcquired, categoryinfo, deptoffice;
    private AlertDialog categoryDialog;
    private static final int CAMERA_REQUEST_CODE = 123;
    private Context fragmentContext;
    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment //
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        submitbtn = view.findViewById(R.id.submitbtn);
        cancelbtn = view.findViewById(R.id.cancelbtn);
        categorylist = view.findViewById(R.id.categorylist);
        deptofficelist = view.findViewById(R.id.deptOfficeList);
        additemimg = view.findViewById(R.id.additemimg);
        generateQR = view.findViewById(R.id.generateQr);
        itemName = view.findViewById(R.id.itemName);
        itemNumber = view.findViewById(R.id.itemNumber);
        modelNumber = view.findViewById(R.id.modelNumber);
        dateAcquired = view.findViewById(R.id.dateAcquired);
        categoryinfo = view.findViewById(R.id.categoryinfo);
        deptoffice = view.findViewById(R.id.deptOffice);
        qrcode = view.findViewById(R.id.qrcode);

        ButtonFunctions();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    additemimg.setImageBitmap(imageBitmap);
                } else {
                    Toast.makeText(requireContext(), "Failed to capture image.", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(requireContext(), "Image capture cancelled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Failed to capture image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            // Handle the case where there's no camera app available
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // CAMERA permission has been granted, you can proceed with opening the camera
                openCamera();
            } else {
                // Permission denied by the user
                // Handle this case, e.g., show a message to the user
            }
        }
    }

    private void ButtonFunctions() {

        additemimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the CAMERA permission is granted
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, so request it
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                } else {
                    // Permission is already granted, you can proceed with opening the camera
                    openCamera();
                }
            }
        });

        generateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemName.getText().toString().isEmpty() && modelNumber.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please input all fields", Toast.LENGTH_SHORT).show();
                } else {
                    generateQRCode();
                }
            }
        });

        deptofficelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the current user //
                String currentUsername = getArguments().getString("currentUsername");

                DatabaseReference deptOfficeRef = FirebaseDatabase.getInstance().getReference().child("inventory").child(currentUsername);
                deptOfficeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            List<String> deptoffices = new ArrayList<>();
                            for (DataSnapshot deptofficesSnapshot : dataSnapshot.getChildren()) {
                                String deptoffice = deptofficesSnapshot.getKey();
                                deptoffices.add(deptoffice);
                            }
                            showDeptOfficesDialog(deptoffices);
                        } else {
                            Toast.makeText(getContext(), "No Department Offices found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        categorylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDeptOffice = deptoffice.getText().toString();
                if (!TextUtils.isEmpty(selectedDeptOffice)) {
                    fetchCategories(selectedDeptOffice);
                } else {
                    Toast.makeText(getContext(), "Please add a Department Office first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deptofficeText = deptoffice.getText().toString();
                String itemNumberText = itemNumber.getText().toString();
                String categorytext = categoryinfo.getText().toString().trim();


                if (itemName.getText().toString().isEmpty() || itemNumberText.isEmpty() || modelNumber.getText().toString().isEmpty() || categoryinfo.getText().toString().isEmpty() || qrcode.getDrawable() == null || deptoffice.getText().toString().isEmpty() || additemimg.getDrawable() == null) {
                    Toast.makeText(getContext(), "Please input all fields", Toast.LENGTH_SHORT).show();
                } else if (dateAcquired.getText().toString().isEmpty() || !isValidDateFormat(dateAcquired.getText().toString())) {
                    if (dateAcquired.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Please enter Date Acquired", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Please enter a valid date format (mm/dd/yyyy)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Get the current user //
                    String currentUsername = getArguments().getString("currentUsername");

                    DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("inventory").child(currentUsername).child(deptofficeText).child(categorytext);
                    Query query = itemsRef.orderByChild("itemNumber").equalTo(itemNumberText);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(getContext(), "Item number already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                uploadImageToStorage();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }

    private void uploadImageToStorage() {
        Drawable qrcodeDrawable = qrcode.getDrawable();
        Drawable itemimgDrawable = additemimg.getDrawable();

        if (qrcodeDrawable != null && itemimgDrawable != null) {
            Bitmap qrcodeBitmap = ((BitmapDrawable) qrcodeDrawable).getBitmap();
            Bitmap itemimgBitmap = ((BitmapDrawable) itemimgDrawable).getBitmap();

            String itemNametext = itemName.getText().toString();
            String deptofficetext = deptoffice.getText().toString();
            String categorytext = categoryinfo.getText().toString();
            String dateAcquiredText = dateAcquired.getText().toString();

            // Get the current user //
            String currentUsername = getArguments().getString("currentUsername");

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Define a common folder path for both qrcode and itemimg images
            StorageReference commonFolderRef = storageRef.child("users").child(currentUsername).child(deptofficetext).child(categorytext).child(itemNametext);

            // Upload qrcode image
            StorageReference qrcodeImageRef = commonFolderRef.child(currentUsername + ".png");
            ByteArrayOutputStream qrcodeBaos = new ByteArrayOutputStream();
            qrcodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, qrcodeBaos);
            byte[] qrcodeImageData = qrcodeBaos.toByteArray();
            UploadTask qrcodeUploadTask = qrcodeImageRef.putBytes(qrcodeImageData);

            // Upload itemimg image
            StorageReference itemimgImageRef = commonFolderRef.child(".png");
            ByteArrayOutputStream itemimgBaos = new ByteArrayOutputStream();
            itemimgBitmap.compress(Bitmap.CompressFormat.PNG, 100, itemimgBaos);
            byte[] itemimgImageData = itemimgBaos.toByteArray();
            UploadTask itemimgUploadTask = itemimgImageRef.putBytes(itemimgImageData);

            // Add listeners for both image uploads
            qrcodeUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle qrcode image upload success
                    qrcodeImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri qrcodeDownloadUri) {
                            // Handle qrcode image URL retrieval
                            // You can save the URL or use it as needed

                            // Get the current user //
                            String currentUsername = getArguments().getString("currentUsername");
                            // Now you can call your custom code
                            saveShopDetails(qrcodeDownloadUri.toString(), currentUsername);
                            voiceCode(qrcodeDownloadUri.toString(), currentUsername);

                            HashMap<String, Object> items = new HashMap<>();
                            items.put("Itemname", itemNametext);
                            items.put("dateacq", dateAcquiredText);
                            items.put("depOffice", deptofficetext);
                            items.put("image", qrcodeImageData);

                            // Create a new CategoryListFragment instance
                            AddItemSaveImageFragment addItemSaveImageFragment = new AddItemSaveImageFragment();
                            Bundle bundle = new Bundle();
                            // Put your data in the bundle as key-value pairs
                            bundle.putSerializable("items", items);
                            // Set the bundle as arguments for the fragment
                            addItemSaveImageFragment.setArguments(bundle);
                            // Get the FragmentManager
                            FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                            // Start a FragmentTransaction
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            // Replace the current fragment with the CategoryListFragment
                            transaction.replace(R.id.fragment_container, addItemSaveImageFragment);
                            transaction.commit();


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle qrcode image upload failure
                    Toast.makeText(getContext(), "QR code upload failed", Toast.LENGTH_SHORT).show();
                }
            });

            itemimgUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle itemimg image upload success
                    itemimgImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri itemimgDownloadUri) {
                            // Handle itemimg image URL retrieval
                            // You can save the URL or use it as needed
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle itemimg image upload failure
                    Toast.makeText(getContext(), "Item image upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "QR code or item image is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void voiceCode(String shopImageUri, String userId) {

        String itemNametext = itemName.getText().toString();
        String itemNumberText = itemNumber.getText().toString();
        String modelNumberText = modelNumber.getText().toString();
        String dateAcquiredText = dateAcquired.getText().toString();
        String category = categoryinfo.getText().toString();
        String deptOffice = deptoffice.getText().toString();

        HashMap<String, Object> itemDetails = new HashMap<>();
        itemDetails.put("itemName", itemNametext);
        itemDetails.put("itemNumber", itemNumberText);
        itemDetails.put("modelNumber", modelNumberText);
        itemDetails.put("dateAcquired", dateAcquiredText);
        itemDetails.put("itemCategory", category);
        itemDetails.put("itemDeptOffice", deptOffice);
        itemDetails.put("shopImageUri", shopImageUri);

        String randomString = generateRandomString();

        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("voiceCode", randomString);
        editor.apply();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("voice code").child(userId).child(randomString);

        databaseRef.setValue(itemDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private static String generateRandomString() {
        StringBuilder randomStringBuilder = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC.length());
            char randomChar = ALPHANUMERIC.charAt(randomIndex);

            // Convert the character to lowercase
            randomChar = Character.toLowerCase(randomChar);

            randomStringBuilder.append(randomChar);
        }

        return randomStringBuilder.toString();
    }

    private void saveShopDetails(String shopImageUri, String userId) {
        String itemNametext = itemName.getText().toString();
        String itemNumberText = itemNumber.getText().toString();
        String modelNumberText = modelNumber.getText().toString();
        String dateAcquiredText = dateAcquired.getText().toString();
        String category = categoryinfo.getText().toString();
        String deptOffice = deptoffice.getText().toString();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("inventory").child(userId).child(deptOffice).child(category).child(itemNametext);

        HashMap<String, Object> itemDetails = new HashMap<>();
        itemDetails.put("itemNumber", itemNumberText);
        itemDetails.put("modelNumber", modelNumberText);
        itemDetails.put("dateAcquired", dateAcquiredText);
        itemDetails.put("shopImageUri", shopImageUri);

        databaseRef.setValue(itemDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(fragmentContext, "Item details added successfully", Toast.LENGTH_SHORT).show();


                        itemName.setText("");
                        itemNumber.setText("");
                        modelNumber.setText("");
                        dateAcquired.setText("");
                        categoryinfo.setText("");
                        deptoffice.setText("");
                        qrcode.setImageBitmap(null);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to save item details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidDateFormat(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", new Locale("en", "PH"));
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void showDeptOfficesDialog(List<String> deptoffices)    {
        String[] deptOfficesArray = deptoffices.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Dept./Office");
        builder.setSingleChoiceItems(deptOfficesArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedDeptOffice = deptOfficesArray[which];
                deptoffice.setText(selectedDeptOffice);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("CANCEL", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showCategoryDialog(List<String> categories) {
        String[] categoryArray = categories.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Category");
        builder.setSingleChoiceItems(categoryArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedCategory = categoryArray[which];
                categoryinfo.setText(selectedCategory);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("CANCEL", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchCategories(String selectedDeptOffice) {

        // Get the current user //
        String currentUsername = getArguments().getString("currentUsername");

        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference()
                .child("inventory")
                .child(currentUsername)
                .child(selectedDeptOffice);

        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> categories = new ArrayList<>();
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String category = categorySnapshot.getKey();
                        categories.add(category);
                    }
                    showCategoryDialog(categories);
                } else {
                    Toast.makeText(getContext(), "No categories found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateQRCode() {
        String itemnameText = itemName.getText().toString();
        String modelNoText = modelNumber.getText().toString();
        String itemnumberText = itemNumber.getText().toString();
        String dateAcqText = dateAcquired.getText().toString();
        String departmentOfficeText = deptoffice.getText().toString();
        String categoryText = categoryinfo.getText().toString();

        String data = itemnameText + "," + itemnumberText + "," + modelNoText + "," + dateAcqText + "," + departmentOfficeText + "," + categoryText;
    
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size.x, size.x);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrCode = barcodeEncoder.createBitmap(bitMatrix);
            qrcode.setImageBitmap(qrCode);


        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (categoryDialog != null && categoryDialog.isShowing()) {
            categoryDialog.dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentContext = context;
    }

}