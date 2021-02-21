package com.example.moneycheck;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class TransactionModel {

    private String note;
    private int balance;
    private boolean status;
    private String timestamp;

    public TransactionModel()
    {

    }

    public TransactionModel(String note, int balance, boolean status, String timestamp) {
        this.note = note;
        this.balance = balance;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
