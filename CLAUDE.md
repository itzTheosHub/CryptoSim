# CryptoSim — Project Instructions & Task Plan

**Courses:** CS3003 (Programming Languages) + CS4092 (Database Design & Development)  
**Deadline:** July 31, 2026 at 11:59pm  
**Student:** Theo Colosimo (solo project)  
**Working directory:** `C:\dev\CryptoSim\CryptoSim\`

---

## Project Summary

CryptoSim is a Java CLI application simulating a single-node blockchain cryptocurrency.
Users register, create wallets (EC key pairs), send signed transactions, and mine blocks
via proof-of-work. Everything persists to PostgreSQL via JDBC. No external dependencies
beyond the PostgreSQL JDBC driver.

This project satisfies both courses simultaneously:
- **CS3003** — OOP paradigm in Java (inheritance, polymorphism, interfaces, encapsulation)
- **CS4092** — Relational DB lifecycle (requirements → ER diagram → schema → SQL → business logic)

---

## Tech Stack

- Java 17+
- PostgreSQL 16
- JDBC (`org.postgresql:postgresql`) — only external dependency
- GitHub (active commits required for +10 extra credit on both courses)

---

## Grading Requirements Checklist

### CS4092 — Database Design
- [ ] Phase 1 — Requirements document (`docs/requirements.pdf`)
- [ ] Phase 2 — ER Diagram (`docs/er-diagram.png`)
- [ ] Phase 3 — Relational Schema (`docs/relational-schema.pdf`)
- [ ] Phase 4 — `sql/schema.sql` (CREATE TABLE + sample INSERTs)
- [ ] 3+ SQL queries (at least one multi-table join)
- [ ] Business logic via Java CLI
- [ ] Video demonstration

### CS3003 — Programming Languages
- [ ] Java application using OOP paradigm
- [ ] Video walkthrough connecting to course concepts
- [ ] GitHub repo with source code

### Extra Credit (both courses)
- [ ] Active GitHub use: frequent commits, meaningful messages, branches

---

## Data Model (4 tables — meets solo minimum)

| Table        | Purpose                                         | Key Relationships                          |
|--------------|-------------------------------------------------|--------------------------------------------|
| Users        | Registered participants                         | user_id PK, unique username                |
| Wallets      | Cryptographic key pairs + derived addresses     | address PK, FK → Users (one-to-many)       |
| Blocks       | The blockchain                                  | block_id PK, self-ref FK → previous_block  |
| Transactions | Transfers + coinbase rewards                    | transaction_id PK, FK → Blocks, FK → Wallets (sender/receiver) |

**Note:** Balances are computed (sum received − sum sent), never stored.

---

## Planned Source Layout

```
sql/
  schema.sql              ← CREATE TABLE + sample INSERTs + SQL queries

docs/
  requirements.pdf        ← CS4092 Phase 1
  er-diagram.png          ← CS4092 Phase 2
  relational-schema.pdf   ← CS4092 Phase 3
  architecture.puml       ← PlantUML source
  architecture.png        ← Rendered class diagram

src/cryptosim/
  Main.java               ← CLI menu loop
  domain/
    Blockchain.java       ← Chain list + mempool + validation rules
    Block.java            ← Self-hashing block container
    Transaction.java      ← Abstract base (TransferTransaction, CoinbaseTransaction extend this)
    TransferTransaction.java
    CoinbaseTransaction.java
    Wallet.java           ← EC key pair, signing, address derivation from public key
    ConsensusStrategy.java ← Interface for pluggable consensus
    ProofOfWork.java      ← Nonce-search mining loop
  storage/
    PostgresStorage.java  ← All JDBC/SQL lives here
```

---

## Day-by-Day Task Plan

### Day 1 — Jul 20 (Sun) — Setup + Requirements
- [x] Create `sql/` directory
- [x] Added `pom.xml` with PostgreSQL JDBC dependency (Maven, Java 17)
- [x] Installed JDK 17 (Eclipse Temurin, located at `C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot`)
- [x] Added JDK to user PATH (`C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin`)
- [ ] Restart VS Code so integrated terminal picks up new PATH — **left off here**
- [ ] Create `src/cryptosim/`, `src/cryptosim/domain/`, `src/cryptosim/storage/` directories
- [ ] Set up local PostgreSQL database (`cryptosim` database + user)
- [ ] Write `docs/requirements.pdf` — CS4092 Phase 1  
      *(Can be plain text → export as PDF; cover all 12 FRs, user roles, data requirements)*
- [ ] Commit everything with a meaningful message

### Day 2 — Jul 21 (Mon) — ER Diagram + Relational Schema
- [ ] Create ER diagram using [dbdiagram.io](https://dbdiagram.io) or draw.io  
      *(Entities: Users, Wallets, Blocks, Transactions — show PKs, FKs, cardinalities)*
- [ ] Export as `docs/er-diagram.png`
- [ ] Write relational schema document (`docs/relational-schema.pdf`)  
      *(List each relation with attributes, PK underlined, FK noted)*
- [ ] Commit

### Day 3 — Jul 22 (Tue) — SQL Schema Implementation
- [x] Write `sql/schema.sql`:
  - [x] `CREATE TABLE users`
  - [x] `CREATE TABLE wallets`
  - [x] `CREATE TABLE blocks`
  - [x] `CREATE TABLE transactions`
  - [x] All constraints: PKs, FKs, NOT NULL, UNIQUE, CHECK (amount > 0)
- [ ] Write sample `INSERT` statements (≥ 2 users, 3 wallets, 2 blocks, 4 transactions)
- [ ] Test schema loads cleanly in PostgreSQL: `psql -d cryptosim -f sql/schema.sql`
- [ ] Commit

### Day 4 — Jul 23 (Wed) — Java: Wallet + Transactions
- [ ] `Wallet.java` — EC key pair generation (`KeyPairGenerator`), SHA-256 + Base58 address derivation, `sign(byte[])`, `verify(byte[], byte[])`
- [ ] `Transaction.java` — abstract base with `transaction_id`, `amount`, `timestamp`, abstract `getType()`
- [ ] `TransferTransaction.java` — extends Transaction, adds `senderAddress`, `receiverAddress`, `signature`
- [ ] `CoinbaseTransaction.java` — extends Transaction, adds `minerAddress`, no signature required
- [ ] Commit

### Day 5 — Jul 24 (Thu) — Java: Block + Mining
- [ ] `ConsensusStrategy.java` — interface with `mine(Block)` method
- [ ] `Block.java` — fields: `blockId`, `previousHash`, `transactions`, `nonce`, `hash`, `minerAddress`, `timestamp`; `computeHash()` using SHA-256 over all fields
- [ ] `ProofOfWork.java` — implements `ConsensusStrategy`; nonce-search loop until hash starts with N zeros (difficulty target)
- [ ] Commit

### Day 6 — Jul 25 (Fri) — Java: Blockchain Core
- [ ] `Blockchain.java`:
  - `List<Block> chain` + `List<Transaction> mempool`
  - `addTransaction(TransferTransaction)` — validates signature + sufficient balance (FR-3, FR-4, FR-5)
  - `mine(String minerAddress)` — bundles mempool → new block → proof-of-work → appends (FR-6)
  - `getBalance(String walletAddress)` — computes from confirmed transactions (FR-9)
  - `validateChain()` — recomputes hashes, checks linkage (FR-12)
- [ ] Commit

### Day 7 — Jul 26 (Sat) — Storage Layer
- [ ] `PostgresStorage.java` — JDBC class wrapping all DB operations:
  - `saveUser()`, `saveWallet()`, `saveBlock()`, `saveTransaction()`
  - `loadBlockchain()` — reconstruct chain from DB on startup
  - `getUserByUsername()`, `getWalletsByUser()`
  - `getTransactionHistory(String userAddress)` — FR-10
  - `getMiningLeaderboard()` — FR-11
- [ ] Wire connection string from environment variable or config
- [ ] Test persistence round-trip (save block → restart → reload chain)
- [ ] Commit

### Day 8 — Jul 27 (Sun) — CLI + Full Integration
- [ ] `Main.java` — menu loop covering all 12 FRs:
  - `[1] Register` → FR-1
  - `[2] Create Wallet` → FR-2
  - `[3] Send Coins` → FR-3/4
  - `[4] View Mempool` → FR-5
  - `[5] Mine Block` → FR-6
  - `[6] View Blockchain` → FR-8
  - `[7] Check Balance` → FR-9
  - `[8] Transaction History` → FR-10
  - `[9] Mining Leaderboard` → FR-11
  - `[10] Validate Chain` → FR-12
- [ ] End-to-end test: register → wallet → send → mine → query
- [ ] Commit

### Day 9 — Jul 28 (Mon) — SQL Queries + Polish
- [ ] Add 3+ SQL queries to `sql/schema.sql` (commented section):
  - **Query 1 (multi-table):** Transaction history for a user across all their wallets  
    *(joins Users → Wallets → Transactions)*
  - **Query 2 (multi-table):** Mining leaderboard — blocks mined per user  
    *(joins Users → Wallets → Blocks)*
  - **Query 3:** All transactions in a given block with sender/receiver info
- [ ] Bug fixes from Day 8 testing
- [ ] Commit

### Day 10 — Jul 29 (Tue) — Testing + GitHub Cleanup
- [ ] Full walkthrough test of all 12 FRs
- [ ] Chain validation test (tamper a block hash, confirm it's caught)
- [ ] Clean up GitHub: review commit history, add a meaningful README badge or status
- [ ] Push all branches, close any open issues
- [ ] Commit

### Day 11 — Jul 30 (Wed) — Video Recording
- [ ] Record video walkthrough (~5-10 min):
  - Demo all CLI features (register, wallet, send, mine, query, validate)
  - Show blockchain output and balance computation
  - Explain OOP design choices (CS3003): abstract Transaction, ConsensusStrategy interface, polymorphism
  - Show SQL queries running in psql
  - Briefly walk through the GitHub commit history
- [ ] Upload video to YouTube (unlisted) or attach directly
- [ ] Prepare Canvas submission text (list group members if any — solo here)

### Day 12 — Jul 31 (Thu) — Submit
- [ ] Final review of all deliverables:
  - `docs/requirements.pdf` ✓
  - `docs/er-diagram.png` ✓
  - `docs/relational-schema.pdf` ✓
  - `sql/schema.sql` (schema + inserts + queries) ✓
  - Source code in GitHub ✓
  - Video link ready ✓
- [ ] Submit to Canvas before 11:59pm

---

## Key Design Notes

- **Balance is never stored** — always computed as `SUM(received) - SUM(sent)` from confirmed transactions (FR-9). This is intentional and worth mentioning in the video.
- **ConsensusStrategy is an interface** — good CS3003 talking point (polymorphism; could swap in a different consensus algorithm without changing Blockchain.java).
- **Transaction is abstract** — TransferTransaction and CoinbaseTransaction extend it (inheritance + polymorphism talking point).
- **PostgresStorage is isolated** — all SQL lives in one class; domain objects know nothing about the DB (separation of concerns).
- **Difficulty target** — start with 2 leading zeros for fast demo mining; mention it's configurable.

---

## CS3003 Paradigm Talking Points (for video)

- **OOP:** Inheritance (`Transaction` → `TransferTransaction`, `CoinbaseTransaction`), interface (`ConsensusStrategy`), encapsulation (private keys never exposed outside `Wallet`)
- **Immutability:** Blocks are effectively immutable after mining (any change breaks the hash)
- **Design pattern:** Strategy pattern for consensus (pluggable algorithms)
