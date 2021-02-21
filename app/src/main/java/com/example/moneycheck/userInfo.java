package com.example.moneycheck;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class userInfo {
    private int balance;
    private int give;
    private int get;

    public userInfo() {

    }


    public userInfo(int balance, int gave, int get) {
        this.balance = balance;
        this.give = gave;
        this.get = get;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getGive() {
        return give;
    }

    public void setGive(int give) {
        this.give = give;
    }

    public int getGet() {
        return get;
    }

    public void setGet(int get) {
        this.get = get;
    }
}
