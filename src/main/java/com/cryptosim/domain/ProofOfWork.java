package com.cryptosim.domain;

public class ProofOfWork implements ConsensusStrategy {

    public void mine(Block block){
        String target = "00";
        while(!block.hash.startsWith(target)){
            block.nonce++;
            block.hash = block.calculateHash();
        }
    }
}