package com.example.smartrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String PASSWORD = "password";
    private String userName;
    private String email;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = db.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        Log.i("FIREBASE", "onCreate: FIREBASE");

        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(
            v -> {
                // Check that login is successful
                // If successful, open the main activity
                EditText emailText = findViewById(R.id.email_text);
                String submittedEmail = emailText.getText().toString();
                if(!validateLogin()) {
                    promptLogin(submittedEmail);
                }
                if(validateLogin()){
                    Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                }
            }
        );

        Button registerBtn = findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(
            v -> {
                // Check that login is successful
                // If successful, open the main activity
                EditText emailText = findViewById(R.id.email_text);
                String submittedEmail = emailText.getText().toString();
                promptRegistration(submittedEmail);
            }
        );
    }

    private boolean validateLogin() {
        return mAuth.getCurrentUser() != null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("FIREBASE", "ONSTART");
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Toast.makeText(LoginActivity.this, "Already logged in as " + currentUser.getDisplayName() ,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Login on firebase
     * @param submittedEmail
     */
    public void promptLogin(String submittedEmail){
        String email = submittedEmail;
        String password = PASSWORD;
        final boolean[] loginSuccess = {false};
        if(email == null || email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter an email.",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Bug: Login Button requires 2 taps to log in

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("FIREBASE", "LOGIN");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginSuccess[0] = true;
                            Toast.makeText(LoginActivity.this, "Logged in as " + user.getEmail() + loginSuccess[0],
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("FIREBASE", "LOGIN FAILED");
                            Toast.makeText(LoginActivity.this, "Login Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }

    /**
     * Registration on firebase.
     * @param submittedEmail
     */
    public void promptRegistration(String submittedEmail) {
        String email = submittedEmail;
        String password = PASSWORD;

        if(email == null || email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter an email.",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("REGISTER", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Registered as " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("REGISTER", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Registration Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }

}