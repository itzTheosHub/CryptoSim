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
    public_key TEXT NOT NULL,
    user_id INT NOT NULL REFERENCES users(user_id), 
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE blocks (
    block_id SERIAL PRIMARY KEY,
    previous_block_id INT REFERENCES blocks(block_id),
    miner_address VARCHAR(100) NOT NULL REFERENCES wallets(address),
    height INT NOT NULL UNIQUE,
    hash VARCHAR(64) NOT NULL UNIQUE,
    previous_hash VARCHAR(64),
    nonce BIGINT NOT NULL,
    mined_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    sender_address VARCHAR(100) REFERENCES wallets(address),
    receiver_address VARCHAR(100) NOT NULL REFERENCES wallets(address),
    block_id INT NOT NULL REFERENCES blocks(block_id),
    type VARCHAR(100) NOT NULL CHECK (type IN ('TRANSFER', 'REWARD')),
    amount NUMERIC(20, 8) NOT NULL CHECK (amount > 0),
    signature TEXT
);