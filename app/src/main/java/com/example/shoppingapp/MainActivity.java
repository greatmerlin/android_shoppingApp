package com.example.shoppingapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.shoppingapp.maps.MapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.SignInMethodQueryResult;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private EditText email, pass;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        email = findViewById(R.id.emailLogin);
        pass = findViewById(R.id.passwordLogin);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnMaps = findViewById(R.id.btnShops);
        findViewById(R.id.signUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailField = email.getText().toString().trim();
                String passField = pass.getText().toString().trim();
                if (TextUtils.isEmpty(emailField)) {
                    email.setError(getString(R.string.requiredField));
                    return;
                }
                if (TextUtils.isEmpty(passField)) {
                    pass.setError(getString(R.string.requiredField));
                    return;
                }
                progressDialog.setMessage(getString(R.string.process));
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(emailField, passField).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                pass.setError(getString(R.string.wrong_pass));
                                pass.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                            checkEmail();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMaps();
            }
        });

        if (getIntent().getBooleanExtra(getString(R.string.exitBoolean), false)) {
            finish();
            return;
        }
    }

    private void checkEmail() {
        firebaseAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                try {
                    boolean emailIsNotInUse = task.getResult().getSignInMethods().isEmpty();
                    if (emailIsNotInUse) {
                        email.setError(getString(R.string.user_d_exist));
                        email.requestFocus();
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    private void goToMaps() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void clickSignUp(View v) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    public void exitApp(View view) {
        while (true) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.geC))
                    .setSmallIcon(R.drawable.ic_message)
                    .setContentTitle(getString(R.string.reminder_first))
                    .setContentText(getString(R.string.reminder_sec))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(100, builder.build());
            break;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.geCs);
            String description = getString(R.string.channelG);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.geNote), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(getString(R.string.exitOKD), true);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}