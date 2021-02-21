package com.example.moneycheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class verifyOTP extends AppCompatActivity {

    private EditText mOTP;
    private Button mVerifyOTP;
    private TextView mOTPFeedback;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        mOTP = findViewById(R.id.otp_editText);
        mVerifyOTP = findViewById(R.id.GenerateOTPButton);
        mOTPFeedback = findViewById(R.id.otp_feedback);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();


        mVerificationId = getIntent().getStringExtra("VerificationId");

        mVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String OTP = mOTP.getText().toString();

                if(OTP.isEmpty())
                {
                    mOTPFeedback.setText("please fill in the otp to continue");
                }
                else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, OTP);
                    signInWithPhoneAuthCredential(credential);

                }


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser!=null)
        {
            sendUserToHome();
        }
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(verifyOTP.this, new OnCompleteListener<AuthResult>() {
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
                                        Toast.makeText(verifyOTP.this, "database created", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(verifyOTP.this, "database not created", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            sendUserToHome();

                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                               mOTPFeedback.setText("There was an error verifying otp");
                            }
                        }
                    }
                });
    }

    public void sendUserToHome()
    {
        Intent intent = new Intent(verifyOTP.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}