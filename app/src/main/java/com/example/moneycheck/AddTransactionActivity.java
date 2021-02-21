package com.example.moneycheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTransactionActivity extends AppCompatActivity {

    private static final String TAG = AddTransactionActivity.class.getSimpleName();
    private boolean sign;
    private String mCustomerId;
    private Button mSave;
    private EditText mNote;
    private EditText mAmount;
    private Button mDatePicker;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);


        mSave = findViewById(R.id.save_button);
        mAmount = findViewById(R.id.amount_editText);
        mNote =findViewById(R.id.transaction_note);
        mDatePicker = findViewById(R.id.datepicker_button);

        Intent intent = getIntent();
        sign = intent.getBooleanExtra("sign",true);
        mCustomerId = intent.getStringExtra("document id");

        if(sign) {
            mAmount.setHint("You Got");
        }
        else {
            mAmount.setHint("You Gave");
        }


        long today = MaterialDatePicker.todayInUtcMilliseconds();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDateAndTime = simpleDateFormat.format(new Date());


        mDatePicker.setText(currentDateAndTime);


        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();

        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointBackward.now());

        builder.setSelection(today);
        builder.setCalendarConstraints(constraintBuilder.build());

        final MaterialDatePicker materialDatePicker = builder.build();


        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(),"DatePicker");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                mDatePicker.setText(materialDatePicker.getHeaderText());
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransaction();
            }
        });

    }

    void addTransaction()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        int moneyAdded =Integer.parseInt(mAmount.getText().toString());


        TransactionModel transactionItem = new TransactionModel();

        transactionItem.setBalance(moneyAdded);
        transactionItem.setNote(mNote.getText().toString());
        transactionItem.setTimestamp(mDatePicker.getText().toString());
        transactionItem.setStatus(sign);

        db.collection("users").document(mCurrentUser.getUid())
                .collection("customers").document(mCustomerId)
                .collection("transaction").add(transactionItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: Document created");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Document not created");
                    }
                });



    
        if(sign)
        {
            db.collection("users").document(mCurrentUser.getUid())
                    .collection("customers").document(mCustomerId)
                    .update("due", FieldValue.increment(moneyAdded));

            db.collection("users").document(mCurrentUser.getUid())
                    .update("give",FieldValue.increment(moneyAdded),"balance",FieldValue.increment(moneyAdded))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                        }
                    });
        }
        else
        {
            db.collection("users").document(mCurrentUser.getUid())
                    .collection("customers").document(mCustomerId)
                    .update("due", FieldValue.increment(-moneyAdded));
            db.collection("users").document(mCurrentUser.getUid())
                    .update("get",FieldValue.increment(moneyAdded),"balance",FieldValue.increment(-moneyAdded))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                        }
                    });
        }

    }

}