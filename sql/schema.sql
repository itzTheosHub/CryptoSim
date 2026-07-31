DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS blocks;
DROP TABLE IF EXISTS wallets;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE wallets (
    address VARCHAR(100) PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE blocks (
    block_id SERIAL PRIMARY KEY,
    previous_block_id VARCHAR(64),
    miner_address VARCHAR(100) NOT NULL,
    hash VARCHAR(64) NOT NULL,
    nonce INT NOT NULL,
    mined_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE transactions (
    transaction_id VARCHAR(100) PRIMARY KEY,
    block_id INT NOT NULL REFERENCES blocks(block_id),
    type VARCHAR(20) NOT NULL CHECK (type IN ('TRANSFER', 'COINBASE')),
    amount NUMERIC(20, 8) NOT NULL CHECK (amount > 0),
    sender_address VARCHAR(100),
    receiver_address VARCHAR(100),
    miner_address VARCHAR(100),
    signature TEXT
);


------------------- Sample Static INSERT statements ----------------------------
-- Sample users
INSERT INTO users (username) VALUES ('Theo');
INSERT INTO users (username) VALUES ('Test-0');


-- Sample wallets
INSERT INTO wallets (address, user_id) VALUES ('abc123456789', 1);

-- Sample blocks
INSERT INTO blocks (previous_block_id, miner_address, hash, nonce)
VALUES (NULL, 'abc123456789', '00a1b2hsghjk', 4821);

-- Sample transactions
INSERT INTO transactions(transaction_id, block_id, type, amount, miner_address)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1, 'COINBASE', 50.0, 'abc123456789');


--------------------- Display All Tables -----------------------------------------
SELECT * FROM users;
SELECT * FROM wallets;
SELECT * FROM blocks;
SELECT * FROM transactions;


-- Query 1: All transactions in a given block
SELECT * FROM transactions WHERE block_id = 1;

-- Query 2: Total coins mined per miner address
SELECT miner_address, COUNT(*) AS blocks_mined
FROM blocks
GROUP BY miner_address
ORDER BY blocks_mined DESC;

-- Query 3: Transaction history for a wallet (multi-table join)
SELECT t.transaction_id, t.type, t.amount, t.sender_address, t.receiver_address, b.mined_at
FROM transactions t
JOIN blocks b ON t.block_id = b.block_id
WHERE t.sender_address = 'WALLET_ADDRESS'
   OR t.receiver_address = 'WALLET_ADDRESS';
