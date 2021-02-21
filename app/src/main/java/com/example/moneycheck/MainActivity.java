package com.example.moneycheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity implements BottomSheetDialog.BottomSheetListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseUser mCurrentUser;

    private Button logout;
    private FirebaseAuth mAuth;
    private TextView mBalance;
    private TextView mGive;
    private TextView mGet;
    private FloatingActionButton mAdd;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView mCustomerList;
    private String UserID;

    FirebaseFirestore datab = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mCustomerList = findViewById(R.id.customerListRV);
        logout = findViewById(R.id.logout_button);
        mBalance = findViewById(R.id.balance_tv);
        mGet = findViewById(R.id.get_tv);
        mGive = findViewById(R.id.give_tv);
        mAdd = findViewById(R.id.whatsapp_fb);



        if(mCurrentUser==null)
        {
            sendUserToSignIn();
        }


        if(mCurrentUser.getUid()!=null)
        {
            UserID = mCurrentUser.getUid();
        }



        Query query =FirebaseFirestore.getInstance().collection("users").document(UserID)
                .collection("customers");

        FirestoreRecyclerOptions<CustomerModel> options = new FirestoreRecyclerOptions.Builder<CustomerModel>().setQuery(query, CustomerModel.class).build();

         adapter = new FirestoreRecyclerAdapter<CustomerModel, CustomerViewHolder>(options) {
            @NonNull
            @Override
            public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item,parent,false);

                return new CustomerViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CustomerViewHolder holder, final int position, @NonNull final CustomerModel model) {
                holder.name.setText(model.getName()+"");
                holder.due.setText(model.getDue()+"");
             //   holder.timestamp.setText(model.getTimestamp().toDate().toString().substring(0,10));
                holder.phoneNo.setText(model.getPhoneNo()+"");

                model.setDocumentId(getSnapshots().getSnapshot(position).getReference().getId());


                holder.mCustomerLists.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this,CustomerActivity.class);
                        intent.putExtra("Customer details",model);

                        startActivity(intent);
                    }
                });

            }

        };
       // mCustomerList.setHasFixedSize(true);
        mCustomerList.setAdapter(adapter);
        mCustomerList.setLayoutManager(new LinearLayoutManager(this));


        adapter.startListening();


        //Displaying the users balance
        DocumentReference docRef = datab.collection("users").document(UserID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    userInfo user_Info = document.toObject(userInfo.class);

                    mBalance.setText(Integer.toString(user_Info.getBalance()));
                    mGet.setText(Integer.toString(user_Info.getGet()));
                    mGive.setText(Integer.toString(user_Info.getGive()));

                }
                else
                {
                    Toast.makeText(MainActivity.this, "error getting document", Toast.LENGTH_SHORT).show();
                }
            }
        });



        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.show(getSupportFragmentManager(),"examplebottomsheet");

            }
        });



        //logout Button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                sendUserToSignIn();

            }
        });
    }


    private class CustomerViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView phoneNo;
        private TextView due;
        private TextView timestamp;
        private ConstraintLayout mCustomerLists;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.customer_name);
            timestamp = itemView.findViewById(R.id.customer_timestamp);
            due = itemView.findViewById(R.id.customer_balance);
            phoneNo= itemView.findViewById(R.id.customer_sign);
            mCustomerLists = itemView.findViewById(R.id.customer_items);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mCurrentUser==null)
        {
            sendUserToSignIn();
        }

      adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    @Override
    public void onButtonClicked(String name,String phoneNo) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CustomerModel customerItem = new CustomerModel();

        customerItem.setDue(0);
        customerItem.setName(name);
        customerItem.setPhoneNo(phoneNo);
    //    customerItem.setTimestamp(firebase.FieldValue.serverTimestamp());


        db.collection("users").document(mCurrentUser.getUid())
                .collection("customers").add(customerItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(MainActivity.this,CustomerActivity.class);

                        Log.d(TAG, "onSuccess: "+documentReference.getId());
                        customerItem.setDocumentId(documentReference.getId());

                        intent.putExtra("Customer details",customerItem);
                        startActivity(intent);
                    }
                });
        /* .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                     @Override
                     public void onSuccess(DocumentReference documentReference) {
                         Intent intent = new Intent(MainActivity.this,CustomerActivity.class);
                         startActivity(intent);
               //        Toast.makeText(MainActivity.this, "DocumentSnapshot added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }
                  })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();
                    }
                });*/

    }

    public void sendUserToSignIn()
    {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

}