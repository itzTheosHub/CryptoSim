package com.cryptosim;

import com.cryptosim.domain.*;
import com.cryptosim.storage.PostgresStorage;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PostgresStorage storage = new PostgresStorage();
        Blockchain blockchain = storage.loadBlockchain();
        ArrayList<Wallet> wallets = new ArrayList<>();

        System.out.println("=== CryptoSim ===");

        boolean running = true;
        while (running) {
            System.out.println("\n[1] Create Wallet");
            System.out.println("[2] Mine Block (earn coins)");
            System.out.println("[3] Send Coins");
            System.out.println("[4] Check Balance");
            System.out.println("[5] View Blockchain");
            System.out.println("[6] Validate Chain");
            System.out.println("[7] Transaction History");
            System.out.println("[8] Exit");
            System.out.print("Choose: ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid input — enter a number.");
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter user ID: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine();
                    Wallet wallet = new Wallet(userId);
                    wallets.add(wallet);
                    System.out.println("Wallet created! Address: " + wallet.getAddress());
                    break;

                case 2:
                    if (wallets.isEmpty()) {
                        System.out.println("Create a wallet first.");
                        break;
                    }
                    System.out.print("Miner wallet index: ");
                    int minerIdx = scanner.nextInt();
                    scanner.nextLine();
                    Wallet miner = wallets.get(minerIdx);
                    blockchain.mine(miner.getAddress(), new ArrayList<>());
                    Block emptyBlock = blockchain.getChain().get(blockchain.getChain().size() - 1);
                    storage.saveBlock(emptyBlock);
                    for (Transaction t : emptyBlock.getBlockData()) {
                        storage.saveTransaction(t, emptyBlock.block_id);
                    }
                    System.out.println("Mined! Wallet " + minerIdx + " earned 50 coins.");
                    break;

                case 3:
                    if (wallets.size() < 2) {
                        System.out.println("Need at least 2 wallets to send coins.");
                        break;
                    }
                    System.out.print("Sender wallet index: ");
                    int senderIdx = scanner.nextInt();
                    System.out.print("Receiver wallet index: ");
                    int receiverIdx = scanner.nextInt();
                    System.out.print("Amount: ");
                    float amount = scanner.nextFloat();
                    scanner.nextLine();

                    Wallet sender = wallets.get(senderIdx);
                    Wallet receiver = wallets.get(receiverIdx);

                    TransferTransaction tx = new TransferTransaction(amount, sender.getAddress(), receiver.getAddress());
                    ArrayList<Transaction> txList = new ArrayList<>();
                    txList.add(tx);

                    blockchain.mine(sender.getAddress(), txList);

                    Block minedBlock = blockchain.getChain().get(blockchain.getChain().size() - 1);
                    storage.saveBlock(minedBlock);
                    for (Transaction t : minedBlock.getBlockData()) {
                        storage.saveTransaction(t, minedBlock.block_id);
                    }
                    break;

                case 4:
                    System.out.print("Enter wallet address: ");
                    String address = scanner.nextLine();
                    System.out.println("Balance: " + blockchain.getBalance(address));
                    break;

                case 5:
                    for (Block block : blockchain.getChain()) {
                        System.out.println("\nBlock ID: " + block.block_id);
                        System.out.println("Hash:     " + block.hash);
                        System.out.println("Prev:     " + block.previous_hash);
                        System.out.println("Nonce:    " + block.nonce);
                    }
                    break;

                case 6:
                    boolean valid = blockchain.validateChain();
                    System.out.println("Chain valid: " + valid);
                    break;

                case 7:
                    System.out.print("Enter wallet address: ");
                    String histAddress = scanner.nextLine();
                    ArrayList<Transaction> history = storage.getTransactionHistory(histAddress);
                    for (Transaction t : history) {
                        System.out.println(t.getType() + " | Amount: " + t.getAmount() + " | ID: " + t.getTransactionId());
                    }
                    break;

                case 8:
                    running = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }

        scanner.close();
    }
}
