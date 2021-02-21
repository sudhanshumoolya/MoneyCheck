package com.example.moneycheck;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class TransactionAdaptor extends FirestoreRecyclerAdapter<TransactionModel, TransactionAdaptor.TransactionHolder> {


    public TransactionAdaptor(@NonNull FirestoreRecyclerOptions<TransactionModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TransactionHolder holder, int position, @NonNull TransactionModel model) {
        holder.timestamp.setText(model.getTimestamp());
        holder.note.setText(model.getNote());
        if(model.isStatus())
        {
            holder.got.setText(model.getBalance()+"");
            holder.gave.setText("");
            holder.dividerLabel.setBackgroundColor(Color.parseColor("#1EB980"));
        }
        else
        {
            holder.got.setText("");
            holder.gave.setText(model.getBalance()+"");
            holder.dividerLabel.setBackgroundColor(Color.parseColor("#FF6859"));
        }

    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item,parent,false);
        return new TransactionHolder(view);
    }

    class TransactionHolder extends RecyclerView.ViewHolder{
        TextView timestamp;
        TextView gave;
        TextView got;
        TextView note;
        View dividerLabel;

        public TransactionHolder(@NonNull View itemView) {
            super(itemView);

            note = itemView.findViewById(R.id.note_transaction);
            timestamp = itemView.findViewById(R.id.timestamp_transaction);
            gave = itemView.findViewById(R.id.money_gave);
            got = itemView.findViewById(R.id.money_got);
            dividerLabel = itemView.findViewById(R.id.transaction_divider);

        }
    }

}
