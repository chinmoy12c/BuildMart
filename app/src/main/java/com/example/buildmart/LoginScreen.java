package com.example.buildmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LoginScreen extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, signupButton;
    private TextView loginBanner, signupPrompt, loginPrompt;
    private CardView appLogo;
    private Animation rotateAnim;

    private FirebaseAuth firebaseAuth;
    private FireStoreHandler fireStoreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        loginPrompt = findViewById(R.id.loginPrompt);
        signupPrompt = findViewById(R.id.signupPrompt);
        loginBanner = findViewById(R.id.loginBanner);
        appLogo = findViewById(R.id.appLogo);
        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStoreHandler = new FireStoreHandler(this);
    }

    public void showSignup(View view) {
        loginButton.setVisibility(View.INVISIBLE);
        signupButton.setVisibility(View.VISIBLE);
        loginPrompt.setVisibility(View.VISIBLE);
        signupPrompt.setVisibility(View.INVISIBLE);
        loginBanner.setText("Sign Up");
    }

    public void showLogin(View view) {
        loginButton.setVisibility(View.VISIBLE);
        signupButton.setVisibility(View.INVISIBLE);
        loginPrompt.setVisibility(View.INVISIBLE);
        signupPrompt.setVisibility(View.VISIBLE);
        loginBanner.setText("Login");
    }

    boolean sanitizeInput(String email, String password) {

        email = email.trim();
        password = password.trim();

        if (email.length() == 0) {
            makeToast("Please enter email");
            return false;
        }

        if (password.length() == 0) {
            makeToast("Please enter password");
            return false;
        }

        if (!email.contains("@")) {
            makeToast("Please enter a valid email address");
            return false;
        }

        return true;
    }

    void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void proceedLogin(View view) {
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (sanitizeInput(email, password)) {

            appLogo.startAnimation(rotateAnim);

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            handleErrors(e);
                        }
                    });
        }
    }

    public void proceedSignup(View view) {

        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (sanitizeInput(email, password)) {

            appLogo.startAnimation(rotateAnim);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            fireStoreHandler.addUserCart(email);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            handleErrors(e);
                        }
                    });
        }

    }


    private void handleErrors(Exception e) {
        appLogo.clearAnimation();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
