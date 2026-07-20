# README - CryptoSim

**University of Cincinnati**

**CS3003/CS4092 — Programming Languages & Database Design & Development, Summer 2026**
---

## Revision History

| Date       | Version | Description                          | Author |
|------------|---------|--------------------------------------|--------|
| 07/20/2026 | 0.1     | Initial README Draft             | Theo Colosimo   |


---

# Overview

CryptoSim is a Java application that simulates a simplified blockchain based cryptocurrency. Users can register, create wallets, and send coins to another; transactions are cryptographically signed, collected into blocks through proof-of-work mining process, and persisted to a PostgreSQL database. The system allows anyone to view the blockchain, query balances and transaction history, and validate the integrity of the chain. 

This project simulates a single node system. It models the core data structures and validation rules of a blockchain without P2P networking or real monetary value.

---

## User Roles

- [ ] User: A registered participant who may own one or more wallets, send coins from their wallets, and query balances and history.
- [ ] Miner: Any user who chooses to mine. Mining confirms pending transactions into a new block and grants the miner a fixed coin reward ( how new coins enter the system).

Both roles interact with the system through the same command-line

## Functional Requirements

- **FR-1** Registration — The system should allow a new user to register with a unique username
- **FR-2** Create Wallet - The system should allow a registered user to create one or more wallets. Each wallet is assigned a cryptographic key pair, and the wallet's address should be derived from it's public key.
- **FR-3** Transactions - The system should allow a user to create a transaction sending a specific amount of coins from one of their wallets to any other wallet address. The transaction should be digitally signed with the sending wallet's private key.
- **FR-4** Transaction Validation — The system should reject a transaction if its signature is invalid or the sending wallet's balance is insufficient.
- **FR-5** Mempool — The system should hold valid, unconfirmed transactions in a pending pool (mempool) until they are mined into a block.
- **FR-6** Mining — The system should allow any user to mine: pending transactions are bundled into a new block, a proof-of-work puzzle is solved (finding a nonce such that the block hash meets the difficulty target), and the block is appended to the chain. A coinbase transaction should award a fixed number of newly created coins to the miner's wallet.
- **FR-7** Persistence — The system should persist all users, wallets, blocks, and transactions to a PostgreSQL database.
- **FR-8** View Blockchain — The system should display the blockchain, showing each block's height, hash, previous hash, miner, and contained transactions.
- **FR-9** Balance Query — The system should compute a wallet's balance as the sum of coins received minus coins sent, derived from confirmed transactions (balances are not stored).
- **FR-10** Transaction History — The system should display the transaction history for a given user across all of their wallets.
- **FR-11** Mining Leaderboard — The system should display a leaderboard showing the number of blocks mined per user.
- **FR-12** Chain Validation — The system should validate the full chain on demand by recomputing each block's hash and verifying each block correctly references its predecessor, reporting any block whose stored data has been altered.



## Data Model


## Tech Stack


