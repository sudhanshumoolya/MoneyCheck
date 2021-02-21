package com.example.moneycheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    BottomSheetListener mListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_bottom_sheet_dialog,container,false);

        Button add = v.findViewById(R.id.add_button);
        final EditText customerName = v.findViewById(R.id.name_et);
        final EditText customerPhoneNo = v.findViewById(R.id.phone_no_ed);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(TextUtils.isEmpty(customerName.getText().toString()))
                {

                    Toast.makeText(getContext(),"Enter the task",Toast.LENGTH_SHORT);
                }
                else {
                    mListener.onButtonClicked(customerName.getText().toString(),customerPhoneNo.getText().toString());
                    dismiss();
                }
            }
        });

        return v;
    }


    public interface BottomSheetListener{
        void onButtonClicked(String name,String phoneNo);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString());
        }
    }
}