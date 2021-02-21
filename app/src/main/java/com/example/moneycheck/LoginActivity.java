package com.example.moneycheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private EditText mPhoneNo;
    private EditText mCountryCode;
    private Button mGenerateOTP;
    private TextView mLoginFeedback;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mPhoneNo = findViewById(R.id.PhoneNoET);
        mCountryCode = findViewById(R.id.CountryCodeET);
        mGenerateOTP = findViewById(R.id.GenerateOTPButton);
        mLoginFeedback = findViewById(R.id.login_feedback);

        mGenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String country_code = mCountryCode.getText().toString();
                String phone_number = mPhoneNo.getText().toString();
                String complete_number = "+" + country_code + "" + phone_number;

                if(country_code.isEmpty()||phone_number.isEmpty())
                {
                    mLoginFeedback.setText("please fill in the form to continue.");
                }
                else
                {
                    mLoginFeedback.setText("Working...");

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            complete_number,
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            mCallBacks
                    );
                }


            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mLoginFeedback.setText("Verification Failed");

            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this,verifyOTP.class);
                        intent.putExtra("VerificationId",s);

                        startActivity(intent);
                    }
                },  10000);


            }
        };



    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mCurrentUser!=null)
        {
            sendUserToHome();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            userInfo user_Info = new userInfo();
                            user_Info.setBalance(0);
                            user_Info.setGive(0);
                            user_Info.setGet(0);

                            db.collection("users").document(mAuth.getCurrentUser().getUid()).set(user_Info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "database created", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "database not created", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            sendUserToHome();

                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mLoginFeedback.setText("There was an error verifying otp");
                            }
                        }
                    }
                });
    }
    public void sendUserToHome()
    {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}