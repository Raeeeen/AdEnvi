package com.rod.adenvi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rod.adenvi.Domains.UserDetailsDomain;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private LinearLayout loginBtn;
    private LinearLayout registerBtn;
    private TextView forgotpassBtn;
    private TextInputEditText usernameTxt;
    private TextInputEditText passwordTxt;
    private TextInputLayout loginPasswordInput, loginEmailInput;
    private ImageView personPic;
    private TextView titletxt;
    LottieAnimationView progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize FirebaseAuth //
        mAuth = FirebaseAuth.getInstance();


        // FullScreen //
        FullScreeen();

        // Check user login status and navigate accordingly
        checkLoginStatusAndNavigate();

        // Button Functions //
        ButtonFunctions();


    }

    // FullScreen //
    private void FullScreeen() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.login_activity);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    // Button Functions and View References //
    private void ButtonFunctions() {
        // View References //
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        forgotpassBtn = findViewById(R.id.forgotpassBtn);
        usernameTxt = findViewById(R.id.usernameTxt);
        passwordTxt = findViewById(R.id.passwordTxt);
        progressBar = findViewById(R.id.progressBar);
        personPic = findViewById(R.id.personPic);
        titletxt = findViewById(R.id.titleTxt);
        loginEmailInput = findViewById(R.id.loginUsernameInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterButtonClick(view);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButtonClick(view);
            }
        });

        forgotpassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    // Register Button Clicked //
    private void RegisterButtonClick(View view) {
            // Show only the progress bar
            progressBar.setVisibility(View.VISIBLE);
            personPic.setVisibility(View.GONE);
            titletxt.setVisibility(View.GONE);
            usernameTxt.setVisibility(View.GONE);
            passwordTxt.setVisibility(View.GONE);
            forgotpassBtn.setVisibility(View.GONE);
            loginBtn.setVisibility(View.GONE);
            registerBtn.setVisibility(View.GONE);
            loginEmailInput.setVisibility(View.GONE);
            loginPasswordInput.setVisibility(View.GONE);

        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    // Login Button Clicked //
    private void LoginButtonClick(View view) {
        String usernametxt = usernameTxt.getText().toString();
        String passwordtxt = passwordTxt.getText().toString();

        if (usernametxt.isEmpty() || passwordtxt.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

            // Show only the progress bar
            progressBar.setVisibility(View.GONE);
            personPic.setVisibility(View.VISIBLE);
            titletxt.setVisibility(View.VISIBLE);
            usernameTxt.setVisibility(View.VISIBLE);
            passwordTxt.setVisibility(View.VISIBLE);
            forgotpassBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            loginEmailInput.setVisibility(View.VISIBLE);
            loginPasswordInput.setVisibility(View.VISIBLE);

            return;
        }

        // Show only the progress bar
        progressBar.setVisibility(View.VISIBLE);
        personPic.setVisibility(View.GONE);
        titletxt.setVisibility(View.GONE);
        usernameTxt.setVisibility(View.GONE);
        passwordTxt.setVisibility(View.GONE);
        forgotpassBtn.setVisibility(View.GONE);
        loginBtn.setVisibility(View.GONE);
        registerBtn.setVisibility(View.GONE);
        loginEmailInput.setVisibility(View.GONE);
        loginPasswordInput.setVisibility(View.GONE);

        DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://inventory-system-96d0e-default-rtdb.firebaseio.com/");

        DatabaseReference userReference = databaseReference.child("users").child(usernametxt);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Show only the progress bar
                progressBar.setVisibility(View.GONE);
                personPic.setVisibility(View.VISIBLE);
                titletxt.setVisibility(View.VISIBLE);
                usernameTxt.setVisibility(View.VISIBLE);
                passwordTxt.setVisibility(View.VISIBLE);
                forgotpassBtn.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
                registerBtn.setVisibility(View.VISIBLE);
                loginEmailInput.setVisibility(View.VISIBLE);
                loginPasswordInput.setVisibility(View.VISIBLE);

                if (snapshot.exists()) {
                    UserDetailsDomain user = snapshot.getValue(UserDetailsDomain.class);

                    if (user != null) {
                        String storedHashedPassword = snapshot.child("passwordHash").getValue(String.class);

                        // Check if the entered password matches the stored hashed password
                        if (BCrypt.verifyer().verify(passwordtxt.toCharArray(), storedHashedPassword).verified) {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Save the username to SharedPreferences
                            SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("usernametxt", usernametxt);
                            editor.apply();

                            // Navigate to NavbarActivity
                            Intent intent = new Intent(LoginActivity.this, NavbarActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "User details not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    // Check user login status and navigate accordingly //
    private void checkLoginStatusAndNavigate() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = preferences.getString("usernametxt", "");

        if (!TextUtils.isEmpty(username)) {
            navigateToNavbar(username);
        }

    }

    // Method to navigate to the NavbarActivity //
    private void navigateToNavbar(String username) {
        Intent intent = new Intent(LoginActivity.this, NavbarActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }



}
