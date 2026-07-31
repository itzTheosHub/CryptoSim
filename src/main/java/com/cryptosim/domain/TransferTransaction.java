package com.cryptosim.domain;

public class TransferTransaction extends Transaction {
    private String senderAddress;
    private String receiverAddress;
    private String signature;


    public TransferTransaction(float amount, String senderAddress, String receiverAddress) {
        super(amount);
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.signature = null;
    }

    public String getSenderAddress(){
        return this.senderAddress;
    }

    public String getReceiverAddress(){
        return this.receiverAddress;
    }

    public String getSignature(){
        return this.signature;
    }

    
    public void setSignature(String signature){
        this.signature = signature;
    }   

    @Override
    public String getType(){
        return "TRANSFER";
    }
}