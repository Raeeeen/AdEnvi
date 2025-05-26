package com.rod.adenvi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rod.adenvi.Domains.UserDetailsDomain;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class RegisterActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    LinearLayout registerBtn, loginBtn;
    TextView titletxt;
    TextInputLayout fullnameInput, regiserUsernameInput, registerPasswordInput;
    TextInputEditText fullnameTxt, usernameTxt, passwordTxt;
    LottieAnimationView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FullScreen //
        FullScreen();

        // Initialize Firebase components //
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://inventory-system-96d0e-default-rtdb.firebaseio.com/");

        // Button Functions //
        ButtonFunctions();
    }

    private void FullScreen() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.register_activity);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    // Button Functions and View References //
    private void ButtonFunctions() {
        // View References //
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);
        fullnameInput = findViewById(R.id.fullnameInput);
        fullnameTxt = findViewById(R.id.fullnameTxt);
        regiserUsernameInput = findViewById(R.id.registerUsernameInput);
        usernameTxt = findViewById(R.id.usernameTxt);
        registerPasswordInput = findViewById(R.id.registerPasswordInput);
        passwordTxt = findViewById(R.id.passwordTxt);
        progressBar = findViewById(R.id.progressBar);
        titletxt = findViewById(R.id.titleTxt);

        // Set a click listener for the Login Button //
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigateToLogin();
            }
        });

        // Set a click listener for the Register Button //
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registration();
            }
        });

    }

    // Navigate to Login Activity with 5sec delay //
    private void NavigateToLogin() {
        // Show only the progress bar
        progressBar.setVisibility(View.VISIBLE);
        titletxt.setVisibility(View.GONE);
        fullnameTxt.setVisibility(View.GONE);
        fullnameInput.setVisibility(View.GONE);
        usernameTxt.setVisibility(View.GONE);
        regiserUsernameInput.setVisibility(View.GONE);
        passwordTxt.setVisibility(View.GONE);
        registerPasswordInput.setVisibility(View.GONE);
        registerBtn.setVisibility(View.GONE);
        loginBtn.setVisibility(View.GONE);


        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }


    // Registration user to Firebase Realtime Database //
    private void Registration() {
        String fullnametxt = fullnameTxt.getText().toString();
        String usernametxt = usernameTxt.getText().toString();
        String passwordtxt = passwordTxt.getText().toString();

        // Hash the password using bcrypt
        String hashedPassword = hashPassword(passwordtxt);

        // Firebase structure: user > username > password
        DatabaseReference userReference = databaseReference.child("users").child(usernametxt);

        // Create a User object (you may have a User class)
        UserDetailsDomain user = new UserDetailsDomain(fullnametxt, hashedPassword);

        // Store the hashed password in a single node
        userReference.child("password").setValue(hashedPassword);

        if(usernametxt.isEmpty() || fullnametxt.isEmpty() || passwordtxt.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();

            // Hide the progress bar
            progressBar.setVisibility(View.GONE);
            titletxt.setVisibility(View.VISIBLE);
            fullnameTxt.setVisibility(View.VISIBLE);
            fullnameInput.setVisibility(View.VISIBLE);
            usernameTxt.setVisibility(View.VISIBLE);
            regiserUsernameInput.setVisibility(View.VISIBLE);
            passwordTxt.setVisibility(View.VISIBLE);
            registerPasswordInput.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
        } else {
            // Show only the progress bar
            progressBar.setVisibility(View.VISIBLE);
            titletxt.setVisibility(View.GONE);
            fullnameTxt.setVisibility(View.GONE);
            fullnameInput.setVisibility(View.GONE);
            usernameTxt.setVisibility(View.GONE);
            regiserUsernameInput.setVisibility(View.GONE);
            passwordTxt.setVisibility(View.GONE);
            registerPasswordInput.setVisibility(View.GONE);
            registerBtn.setVisibility(View.GONE);
            loginBtn.setVisibility(View.GONE);

            // Add the user to the database
            userReference.setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {

                    // Hide the progress bar
                    progressBar.setVisibility(View.GONE);
                    titletxt.setVisibility(View.GONE);
                    fullnameTxt.setVisibility(View.GONE);
                    fullnameInput.setVisibility(View.GONE);
                    usernameTxt.setVisibility(View.GONE);
                    regiserUsernameInput.setVisibility(View.GONE);
                    passwordTxt.setVisibility(View.GONE);
                    registerPasswordInput.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.GONE);

                    if (error == null) {
                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    // Method to hash the password using bcrypt
    private String hashPassword(String password) {
        String passwordHashed = password;
        return BCrypt.withDefaults().hashToString(12, passwordHashed.toCharArray());
    }


}
