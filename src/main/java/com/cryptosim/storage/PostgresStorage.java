package com.cryptosim.storage;

import com.cryptosim.domain.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class PostgresStorage {

    private Connection connection;

    public PostgresStorage(){
        connect();
    }
    
    public void connect(){
        try {
            this.connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/cryptosim",
                "cryptosim_user",
                "Password123456!"
            );
            System.out.println("Connected to database!");

        } catch (SQLException e){
            throw new RuntimeException("DB connection failed", e);
        }

    }   

    public void saveBlock(Block block){
        String insert = """
            INSERT INTO blocks (previous_block_id, miner_address, hash, nonce)
            VALUES (?, ?, ?, ?)
            """;

        try {
            PreparedStatement stmt = this.connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, block.previous_hash);
            stmt.setString(2, block.minerAddress);
            stmt.setString(3, block.hash);
            stmt.setInt(4, block.nonce);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                block.block_id = rs.getInt(1);
            }

            System.out.println("Block saved to DB. ID: "  + block.block_id);

        } catch (SQLException e){
            throw new RuntimeException("Failed to save block", e);
        }
        
        

    }

    public void saveTransaction(Transaction tx, int blockId){
        String saveTx = """
                INSERT INTO transactions (block_id, transaction_id, type, amount, sender_address, receiver_address,
                miner_address)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try {
            PreparedStatement stmt = this.connection.prepareStatement(saveTx);
            stmt.setInt(1, blockId);
            stmt.setString(2, tx.getTransactionId());
            stmt.setString(3, tx.getType());
            stmt.setFloat(4, tx.getAmount());

            if (tx.getType().equals("TRANSFER")) {
                TransferTransaction ttx = (TransferTransaction) tx;
                stmt.setString(5, ttx.getSenderAddress());
                stmt.setString(6, ttx.getReceiverAddress());
                stmt.setNull(7, java.sql.Types.VARCHAR);
            } else {
                CoinbaseTransaction ctx = (CoinbaseTransaction) tx;
                stmt.setNull(5, java.sql.Types.VARCHAR);
                stmt.setNull(6, java.sql.Types.VARCHAR);
                stmt.setString(7, ctx.getMinerAddress());
            }
            stmt.executeUpdate();

            System.out.println("Transaction saved: " + tx.getTransactionId() + " | Type: " + tx.getType());


            } catch (SQLException e) {
                throw new RuntimeException("Failed to save transction", e);
            }
    }

    public Blockchain loadBlockchain(){
        Blockchain blockchain = new Blockchain();
        blockchain.getChain().clear();

        try {
            // Load all blocks ordered by block_id
            PreparedStatement blockStmt = connection.prepareStatement(
                "SELECT * FROM blocks ORDER BY block_id ASC"
            );
            ResultSet blockRs = blockStmt.executeQuery();

            while (blockRs.next()) {
                int blockId = blockRs.getInt("block_id");
                String previousHash = blockRs.getString("previous_block_id");
                String minerAddress = blockRs.getString("miner_address");
                String hash = blockRs.getString("hash");
                int nonce = blockRs.getInt("nonce");

                // Load transactions for this block
                PreparedStatement txStmt = connection.prepareStatement(
                    "SELECT * FROM transactions WHERE block_id = ?"
                );
                txStmt.setInt(1, blockId);
                ResultSet txRs = txStmt.executeQuery();

                ArrayList<Transaction> transactions = new ArrayList<>();
                while (txRs.next()) {
                    String type = txRs.getString("type");
                    float amount = txRs.getFloat("amount");

                    if (type.equals("TRANSFER")) {
                        TransferTransaction tx = new TransferTransaction(
                            amount,
                            txRs.getString("sender_address"),
                            txRs.getString("receiver_address")
                        );
                        tx.setSignature(txRs.getString("signature"));
                        transactions.add(tx);
                    } else {
                        transactions.add(new CoinbaseTransaction(amount, txRs.getString("miner_address")));
                    }
                }

                Block block = new Block(transactions, previousHash == null ? "0" : previousHash, minerAddress);
                block.block_id = blockId;
                block.hash = hash;
                block.nonce = nonce;
                blockchain.getChain().add(block);
            }

            System.out.println("Blockchain loaded from DB. Blocks: " + blockchain.getChain().size());

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load blockchain", e);
        }

        return blockchain;
    }


    public ArrayList<Transaction> getTransactionHistory(String walletAddress){
        ArrayList<Transaction> history = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM transactions WHERE sender_address = ? OR receiver_address = ?"
            );
            stmt.setString(1, walletAddress);
            stmt.setString(2, walletAddress);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("type");
                float amount = rs.getFloat("amount");

                if (type.equals("TRANSFER")) {
                    TransferTransaction tx = new TransferTransaction(
                        amount,
                        rs.getString("sender_address"),
                        rs.getString("receiver_address")
                    );
                    history.add(tx);
                } else {
                    history.add(new CoinbaseTransaction(amount, rs.getString("miner_address")));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get transaction history", e);
        }

        return history;
    }
}