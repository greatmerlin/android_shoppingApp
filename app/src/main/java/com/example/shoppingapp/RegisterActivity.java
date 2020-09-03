package com.example.shoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailText;
    private EditText passwordText;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        emailText = findViewById(R.id.emailRegister);
        passwordText = findViewById(R.id.passwordRegister);
        findViewById(R.id.signUp);

        Button btnReg = findViewById(R.id.btnRegister);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailField = emailText.getText().toString().trim();
                String passField = passwordText.getText().toString().trim();
                if (TextUtils.isEmpty(emailField)) {
                    emailText.setError(getString(R.string.requiredField));
                    return;
                }
                if (TextUtils.isEmpty(passField)) {
                    passwordText.setError(getString(R.string.requiredField));
                    return;
                }
                progressDialog.setMessage(getString(R.string.processing));
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(emailField, passField).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            checkEmail();
                            if (!isValidEmail(emailText.getText().toString())) {
                                emailText.setError(getString(R.string.invalid_address_format));
                                emailText.requestFocus();
                            }
                            Log.w(TAG, getString(R.string.createUserEmailFail), task.getException());
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void checkEmail() {
        firebaseAuth.fetchSignInMethodsForEmail(emailText.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                try {
                    boolean emailIsNotInUse = task.getResult().getSignInMethods().isEmpty();
                    if (emailIsNotInUse) {
                        passwordText.setError(getString(R.string.short_pass));
                        passwordText.requestFocus();
                    } else {
                        emailText.setError(getString(R.string.email_exists));
                        emailText.requestFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public void clickSignIn(View v) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}