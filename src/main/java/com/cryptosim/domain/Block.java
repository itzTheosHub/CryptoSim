// | Blocks | The blockchain | block_id PK, self-referencing FK to previous_block_id, FK to miner's wallet |

package com.cryptosim.domain;

import java.util.List;
import com.cryptosim.domain.Transaction;

public class Block {

    public int block_id;
    public String hash;
    public String previous_hash;
    public int nonce;
    public String minerAddress;
    
    private Long timestamp;
    private List<Transaction> data;

    // Block Constructor
    public Block(List<Transaction> data, String previous_hash, String minerAddress){
        this.block_id = 0;
        this.hash = "";
        this.previous_hash = previous_hash;
        this.nonce = 0;
        this.previous_hash = previous_hash;
        this.minerAddress = minerAddress;

        this.timestamp = System.currentTimeMillis();
        this.data = data;
    }

    public List<Transaction> getBlockData() {
        return this.data;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String calculateHash(){
        
        // Gather all the transaction Ids inside a dataString
        String dataString = "";
        for (int i = 0; i < data.size(); i++){
            Transaction t = data.get(i);
            dataString += t.getTransactionId();
        }
        
        // Calculate the SHA-256
        String calculatedHash = StringUtil.applySha256(
            this.previous_hash + Long.toString(this.timestamp) + Integer.toString(nonce) + dataString
        );

        return calculatedHash;
    }

}