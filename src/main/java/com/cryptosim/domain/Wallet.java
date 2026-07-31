package com.cryptosim.domain;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;


public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String address;
    private int userId;

    private String deriveAddress() {
        return StringUtil.applySha256(java.util.Base64.getEncoder().encodeToString(this.getPublicKey().getEncoded()));
    }

    public Wallet(int userId){
        this.userId = userId;

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(256);
            KeyPair keyPair = keyGen.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        }
        catch (Exception e) {
            System.out.println("Error generating keys: Wallet Class Constructor");
            throw new RuntimeException(e);
        }

        this.address = deriveAddress();
    }

    public String getAddress(){
        return this.address;
    }

    public PublicKey getPublicKey(){
        return this.publicKey;
    }

    public byte[] sign(byte[] data) {

        byte[] result = new byte[0];

        try{
            // Hash the data first, then sign the hash using elliptic curve
            Signature sig = Signature.getInstance("SHA256withECDSA");
            sig.initSign(this.privateKey);
            sig.update(data);
            result = sig.sign();
            return result;
        } 
        catch (Exception e){
            System.out.println("Error Generating Signature");
            throw new RuntimeException(e);
        }

    }

    public boolean verify(byte[] data, byte[] signature) {

        boolean result = false;
        
        try {
            Signature sig = Signature.getInstance("SHA256withECDSA");
            sig.initVerify(this.publicKey);
            sig.update(data);
            result = sig.verify(signature);
            return result;
        }
        catch (Exception e) {
            System.out.println("Error Verifying Signature");
            throw new RuntimeException(e);
        }

    }


    

}