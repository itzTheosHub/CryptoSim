package com.cryptosim.domain;
import java.util.UUID;

public abstract class Transaction {
    private String transaction_Id;
    private float amount;
    private long timestamp;

    public Transaction(float amount){
        this.transaction_Id = UUID.randomUUID().toString();
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
    }

    public float getAmount(){
        return this.amount;
    }

    public String getTransactionId(){
        return this.transaction_Id;
    }

    public long getTimestamp(){
        return this.timestamp;
    }

    public abstract String getType();
}