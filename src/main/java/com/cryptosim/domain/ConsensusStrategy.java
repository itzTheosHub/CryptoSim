package com.cryptosim.domain;


public interface ConsensusStrategy {
    void mine(Block block);
}