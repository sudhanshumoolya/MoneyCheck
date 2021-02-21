package com.example.moneycheck;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;


public class CustomerModel implements Parcelable {

    private String documentId;
    private String name;
    private int due;
    private String phoneNo;
    @ServerTimestamp private Timestamp timestamp;


    public CustomerModel()
    {

    }

    public CustomerModel(String name, int due, String phoneNo, Timestamp timestamp) {
        this.name = name;
        this.due = due;
        this.phoneNo = phoneNo;
        this.timestamp = timestamp;
    }


    protected CustomerModel(Parcel in) {
        name = in.readString();
        due = in.readInt();
        phoneNo = in.readString();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        documentId = in.readString();
    }

    public static final Creator<CustomerModel> CREATOR = new Creator<CustomerModel>() {
        @Override
        public CustomerModel createFromParcel(Parcel in) {
            return new CustomerModel(in);
        }

        @Override
        public CustomerModel[] newArray(int size) {
            return new CustomerModel[size];
        }
    };

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public int getDue() {
        return due;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDue(int due) {
        this.due = due;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(due);
        parcel.writeString(phoneNo);
        parcel.writeParcelable(timestamp, i);
        parcel.writeString(documentId);
    }
}
