package com.cryptosim.domain;

import java.util.ArrayList;


public class Blockchain {

    private ArrayList<Block> chain = new ArrayList<>();

    public Blockchain (){
        this.chain = new ArrayList<>();
        Block rootBlock = new Block(new ArrayList<>(), "0", "Root");
        rootBlock.hash = rootBlock.calculateHash();
        chain.add(rootBlock);
    }


    public ArrayList<Block> getChain(){
        return this.chain;
    }

    public int getBalance(String walletAddress){
        int balance = 0;

        // Pass 1: Loop through each block in the chain
        for (Block block : this.getChain()) {
            // Pass 2: Loop through each transaction in the respective block data list 
            for (Transaction t : block.getBlockData()){
                // check if t is of type "TRANSFER"
                if (t.getType().equals("TRANSFER")){
                    // Cast t as a TransferTransaction object
                    TransferTransaction tx = (TransferTransaction) t;
                    // Check if receiverAddr == walletAddr then add then add the ammount in tx
                    if(tx.getReceiverAddress().equals(walletAddress)){
                        balance += tx.getAmount();
                    }
                    // Check if senderAddr == walletAddr then subtract the amount in tx
                    if(tx.getSenderAddress().equals(walletAddress)){
                        balance -= tx.getAmount();
                    }
                }
            }   
        }
        return balance;
    }

    public boolean validateChain(){

        boolean isValid = true;
        for (int i = 0; i < this.chain.size(); i++){
            // Check each block's hash == calculateHash() 
            if (!this.chain.get(i).hash.equals(this.chain.get(i).calculateHash())){
                isValid = false;
            }
            // Make sure to not check root block's prev hash
            if ( i > 0 && !this.chain.get(i).previous_hash.equals(this.chain.get(i-1).hash)) {
                isValid = false;
            }
        }

        return isValid;
    }

    public void mine(String minerAddress, ArrayList<Transaction> transactions){
        String prev_hash = chain.isEmpty() ? "0" : chain.get(chain.size() - 1).hash;
        transactions.add(new CoinbaseTransaction(50.0f, minerAddress));
        Block newBlock = new Block(transactions, prev_hash, minerAddress);
        ProofOfWork proof = new ProofOfWork();
        proof.mine(newBlock);
        chain.add(newBlock);
        System.out.println("Block mined! Hash: " + newBlock.hash);
    }



}