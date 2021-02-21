package com.example.moneycheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CustomerActivity extends AppCompatActivity {

    private TextView mCustomerName;
    private TextView mCustomerBalance;
    private TextView mCustomerPhoneNo;
    private FloatingActionButton mWhatsAppFB;
    private Button mGaveButton;
    private Button mGotButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mCurrentUser = mAuth.getCurrentUser();
    private TransactionAdaptor transactionAdaptor;
    private CollectionReference collectionReference;

    String PhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);



        Intent intent = getIntent();
        final CustomerModel customerModel = intent.getParcelableExtra("Customer details");

        mCustomerName = findViewById(R.id.ind_customer_name);
        mCustomerBalance = findViewById(R.id.ind_customer_balance);
        mCustomerPhoneNo = findViewById(R.id.ind_customer_phoneNo);
        mWhatsAppFB = findViewById(R.id.whatsapp_fb);
        mGaveButton = findViewById(R.id.gave_button);
        mGotButton = findViewById(R.id.got_button);


        mCustomerName.setText(customerModel.getName());
        mCustomerBalance.setText(String.valueOf(customerModel.getDue()));
        mCustomerPhoneNo.setText(customerModel.getPhoneNo());
        PhoneNo =customerModel.getPhoneNo();


        collectionReference = db.collection("users").document(mCurrentUser.getUid())
                .collection("customers").document(customerModel.getDocumentId())
                .collection("transaction");

        setUpRecyclerView();


        mWhatsAppFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean appInstalled;
                appInstalled = appInstalledCheck("com.whatsapp");
                if(appInstalled)
                {
                    Intent whatsAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+PhoneNo+"&text=hello"));
                    startActivity(whatsAppIntent);
                }
                else
                {
                    Toast.makeText(CustomerActivity.this, "U have not installed WhatsApp", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mGotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotIntent = new Intent(CustomerActivity.this, AddTransactionActivity.class);
                gotIntent.putExtra("sign",true);
                gotIntent.putExtra("document id",customerModel.getDocumentId());

                startActivity(gotIntent);
            }
        });

        mGaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gaveIntent = new Intent(CustomerActivity.this,AddTransactionActivity.class);
                gaveIntent.putExtra("sign",false);
                gaveIntent.putExtra("document id",customerModel.getDocumentId());
                startActivity(gaveIntent);
            }
        });


    }

    private void setUpRecyclerView() {
        Query query = collectionReference.orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<TransactionModel> options = new FirestoreRecyclerOptions.Builder<TransactionModel>()
                .setQuery(query,TransactionModel.class).build();

        transactionAdaptor = new TransactionAdaptor(options);

        RecyclerView recyclerView = findViewById(R.id.transaction_RV);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(transactionAdaptor);

    }

    @Override
    protected void onStart() {
        super.onStart();
        transactionAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        transactionAdaptor.stopListening();
    }

    private boolean appInstalledCheck(String uri)
    {
        PackageManager packageManager = getPackageManager();
        boolean appInstalled;

        try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        }catch (PackageManager.NameNotFoundException e)
        {
            appInstalled = false;
        }
        return appInstalled;
    }
}