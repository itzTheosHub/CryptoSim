package com.cryptosim.domain;


public class CoinbaseTransaction extends Transaction {
    private String minerAddress;

    public CoinbaseTransaction(float amount, String minerAddress){
        super(amount);
        this.minerAddress = minerAddress;

    }

    public String getMinerAddress(){
        return this.minerAddress;
    }

    @Override
    public String getType(){
        return "COINBASE";
    }
    
}