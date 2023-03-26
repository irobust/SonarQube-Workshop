package com.globomantics.insecureapp.service;

import org.springframework.orm.jpa.vendor.Database;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class RegistrationService {

    private static final String REGISTER_USER = "INSERT INTO users (uid, pwd) VALUES(?, ?);";

    public boolean registerUser(String uid, String pwd){
        String encryptedPwd = encryptPassword(pwd);

        try {
            Connection conn = DriverManager.getConnection("jdbc:derby:memory:myDB;create=true", "login", "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String className = System.getProperty("messageClassName");
        try {
            Class clazz = Class.forName(className);  // Noncompliant
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    // MITRE ATT&ACK technique T1078 - Valid Accounts
    // https://attack.mitre.org/techniques/T1078
    // OWASP Top 10 2017 Category A2 - Broken Authentication
    // https://www.owasp.org/index.php/Top_10-2017_A2-Broken_Authentication
    private String encryptPassword(String pwd){
        try{
            byte[] magic = {0x4b, 0x47, 0x53, 0x21, 0x40, 0x23, 0x24, 0x25};
            byte[] pwdBytes = pwd.getBytes();

            DESKeySpec dks = new DESKeySpec (makeDesKey (pwdBytes, 0));
            SecretKeyFactory fac = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = fac.generateSecret(dks);

            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] out = cipher.doFinal(magic, 0, 8);

            byte[] encrytedPwdBytes = new byte [21];
            System.arraycopy (out, 0, encrytedPwdBytes, 0, 8);

            return encrytedPwdBytes.toString();
        }
        catch (NoSuchPaddingException nspe){
            throw new AssertionError();
        }
        catch (NoSuchAlgorithmException nsae){
            throw new AssertionError();
        }
        catch (InvalidKeyException | InvalidKeySpecException e){
            throw new AssertionError();
        }
        catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
        catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    byte[] makeDesKey (byte[] input, int off) {
        int[] in = new int [input.length];
        for (int i=0; i<in.length; i++ ) {
            in[i] = input[i]<0 ? input[i]+256: input[i];
        }
        byte[] out = new byte[8];
        out[0] = (byte)in[off+0];
        out[1] = (byte)(((in[off+0] << 7) & 0xFF) | (in[off+1] >> 1));
        out[2] = (byte)(((in[off+1] << 6) & 0xFF) | (in[off+2] >> 2));
        out[3] = (byte)(((in[off+2] << 5) & 0xFF) | (in[off+3] >> 3));
        out[4] = (byte)(((in[off+3] << 4) & 0xFF) | (in[off+4] >> 4));
        out[5] = (byte)(((in[off+4] << 3) & 0xFF) | (in[off+5] >> 5));
        out[6] = (byte)(((in[off+5] << 2) & 0xFF) | (in[off+6] >> 6));
        out[7] = (byte)((in[off+6] << 1) & 0xFF);
        return out;
    }
}
