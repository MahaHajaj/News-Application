package com.example.news.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.news.MainActivity;
import com.example.news.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.etext_email)
    EditText editTextEmail;
    @BindView(R.id.etext_password)
    EditText editTextPassword;
    @BindView(R.id.btn_login)
    Button logIn;
    @BindView(R.id.btn_signup)
    Button signUp;
    @BindView(R.id.btn_reset_password)
    Button resetPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void loginUserAccount() {

        String email, password;
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), R.string.login_successful, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                editTextEmail.setError(getString(R.string.error_invalid_email));
                                editTextEmail.requestFocus();
                            } catch(FirebaseAuthUserCollisionException e) {
                                editTextEmail.setError(getString(R.string.error_user_exists));
                                editTextEmail.requestFocus();
                            } catch(Exception e) {
                                Log.e("log", e.getMessage());
                            }                        }
                    }
                });
    }

}
