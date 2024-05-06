package net.jchad.shared.cryptography;

import net.jchad.shared.cryptography.keys.CrypterKey;
import net.jchad.shared.cryptography.crypterUnits.KeyUnit;
import net.jchad.shared.cryptography.crypterUnits.TagUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.*;



/**
 * <h4>Abstraction of the {@link javax.crypto} and {@link java.security} class</h4>
 * Simplifies the encryption and decryption process by abstracting the {@link javax.crypto default Cryptography library} in java.
 * An example to use the {@code RSA} encryption and decryption:
 * <blockquote><pre>
 * CrypterManager crypterManager = new CrypterManager();
 *         String plainText = "Hello World! I love RSA";
 *         crypterManager.setRemotePublicKey(crypterManager.getPublicKey());
 *         String encryptedText = crypterManager.encryptRSA(plainText);
 *         String decryptedText = crypterManager.decryptRSA(encryptedText);
 *         System.out.println(decryptedText); //Output: Hello World! I love RSA
 * </pre></blockquote>
 * It is important to do {@code crypterManager.setRemotePublicKey(crypterManager.getPublicKey());} if
 * you are encrypting your own messages (and decrypting them later).
 * <p>This has to be done because the {@link CrypterManager CrypterManager} uses the remote Public key for encryption.</p>
 *
 * <p>Here is an example on how to decrypt and encrypt using the Advanced Encryption Standard (The used AES mode is GCM/NoPadding)</p>
 * <blockquote><pre>
 *         String plainTextAES = "Hello World! I love AES";
 *         CrypterManager crypterManager = new CrypterManager();
 *         String encryptedString = crypterManager.encryptAES(plainTextAES);
 *         String decryptedString = crypterManager.decryptAES(encryptedString);
 *         System.out.println(decryptedString); //Output: Helle World! I love AES
 * </pre></blockquote>
 * <p>Here is an example on how to sign and verify a string</p>
 * <blockquote><pre>
 *     String textToSign = "Hello World! I love RSA signatures";
 *         CrypterManager crypterManager = new CrypterManager();
 *         crypterManager.setRemotePublicKey(crypterManager.getPublicKey());
 *         String signature = crypterManager.sign(textToSign);
 *         boolean isSignedCorrectly = crypterManager.verify(signature, textToSign);
 *         System.out.println(isSignedCorrectly); //Output: true
 * </pre></blockquote>
 * <p>As mentioned before it is important to set the remote public key to the current public key,
 * because you are encrypting and decrypting on one "instance"</p>
 */
public class CrypterManager {
    private KeyPair keyPair = null;

    /**
     * The public key of the client you are communicating with.
     * This public key is used for encryption, allowing the other
     * host to decrypt the data using their own private key.
     */
    private PublicKey remotePublicKey = null;
    private SecretKey secretKey = null;
    private String iv = ""; //InitializationVector



    public CrypterManager() {
        this(CrypterUtil.getRandomByteArr(), null, null);
    }
    public CrypterManager(byte[] initializationVector) {
        this(initializationVector, null,null);
    }

    public CrypterManager(byte[] iv, SecretKey secretKey) {
        this(iv, null,  secretKey);
    }

    public CrypterManager(SecretKey secretKey) {
        this(CrypterUtil.getRandomByteArr(), secretKey);
    }

    public CrypterManager(KeyPair keyPair) {
        this(CrypterUtil.getRandomByteArr(), keyPair, null);
    }



    public CrypterManager(PublicKey publicKey, PrivateKey privateKey) {
        this(CrypterUtil.getRandomByteArr(), new KeyPair(publicKey, privateKey), null);
    }



    public CrypterManager(byte[] iv, KeyPair keyPair, SecretKey secretKey) {
        setIV(iv);
        setAESkey(secretKey);
        setKeyPair(keyPair);
    }

    /**
     * Encrypts the given text using the AES/GCM/NoPadding algorithm.
     * If the AES-key/SecretKey wasn't initialized using {@code setAESkey()} a random one gets assigned
     * After encryption the encrypted text gets encoded to Base64
     * @param plainText The text that should get encrypted
     * @return The encrypted plainText encoded in <b>Base64</b>.
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws ImpossibleConversionException
     */
    public String encryptAES(String plainText) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ImpossibleConversionException {
        initAESKey();
        byte[] encryptedByteArray = Crypter.encryptAES(plainText.getBytes(), secretKey, getIV(), TagUnit.DEFAULT);
        String encryptedBase64String = CrypterUtil.bytesToBase64Str(encryptedByteArray);
        return encryptedBase64String;
    }

    /**
     * Decrypts the given text using the AES/GCM/NoPadding algorithm.
     * The given String first gets decoded into base64 and after that it gets encrypted
     * If the AES-key/SecretKey wasn't initialized using {@code setAESkey()} a random one gets assigned
     * Make sure to set the initialize vector with {@code setIV(byte[] iv)}
     * @param encryptedText The base64 encoded and the AES encrypted String
     * @return The encrypted string
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws ImpossibleConversionException
     */
    public String decryptAES(String encryptedText) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ImpossibleConversionException {
        initAESKey();
        byte[] base64DecodedString = CrypterUtil.base64ToBytes(encryptedText.getBytes());
        byte[] decryptedByteText = Crypter.decryptAES(base64DecodedString,secretKey, getIV(), TagUnit.DEFAULT);
        String decryptedStringText = CrypterUtil.bytesToString(decryptedByteText);
        return decryptedStringText;
    }

    /**
     * Encrypts the given text using the provided {@link PublicKey PublicKey} (RSA/NoPadding algorithm)
     * After the encryption process <b>the text will be encoded to Base64</b>
     * @param plainText The text that gets encrypted and then encoded to base64
     * @param publicKey the public key used for encrypting
     * @return The encrypted and base64 encoded String
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public String encryptRSA(String plainText, PublicKey publicKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                byte[] encryptedBytes = Crypter.encryptRSA(plainText.getBytes(), publicKey);
                String encryptedBase64String = CrypterUtil.bytesToBase64Str(encryptedBytes);
                return encryptedBase64String;
    }

    /**
     * Encrypts the given text using the RSA/NoPadding algorithm.
     * <u>The REMOTE public key is used for encryption</u>. <b>BE SURE TO SET IT FIRST WITH</b> {@code setRemotePublicKey(RemotePublicKey)}.
     * After the encryption process <b>the text will be encoded to Base64</b>
     * @param plainText The text that gets encrypted and then encoded to base64
     * @return The encrypted and base64 encoded String
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public String encryptRSA(String plainText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        initRemotePublicKey();
        return encryptRSA(plainText, remotePublicKey);
    }

    /**
     * Decrypts the given text using the RSA/NoPadding algorithm.
     * Before the encryption process <b>the String gets decoded from Base64 into a byte array</b>
     *
     * @param encryptedBase64Text the RSA/NoPadding encrypted and Base64 encoded String
     * @return returns the decrypted and Base64 encoded String
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws ImpossibleConversionException
     */
    public String decryptRSA(String encryptedBase64Text) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ImpossibleConversionException {
        initKeyPair();
        return decryptRSA(encryptedBase64Text, keyPair.getPrivate());

    }
    /**
     * Decrypts the given text using the provided {@link PrivateKey privateKey} (RSA/NoPadding algorithm)
     * Before the encryption process <b>the String gets decoded from Base64 into a byte array</b>
     *
     * @param encryptedBase64Text the RSA/NoPadding encrypted and Base64 encoded String
     * @return returns the decrypted and Base64 encoded String
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws ImpossibleConversionException
     */
    public String decryptRSA(String encryptedBase64Text, PrivateKey privateKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ImpossibleConversionException {
        byte[] decodedBytes = CrypterUtil.base64StrToBytes(encryptedBase64Text);
        byte[] encryptedBytes = Crypter.decryptRSA(decodedBytes, privateKey);
        return CrypterUtil.bytesToString(encryptedBytes);
    }

    /**
     * Signs the provided String.
     * This is done to prevent <b>MITM</b> (Man-in-the-middle attacks) because the server or
     * client can verify that the string was sent from the correct host.
     *
     * @param textToSign The text/string that gets signed
     * @return The signature of the provided private key and text
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    public String sign(String textToSign) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        initKeyPair();
        byte[] signature = Crypter.sign(textToSign.getBytes(), keyPair.getPrivate());
        String encodedSignature = CrypterUtil.bytesToBase64Str(signature);
        return encodedSignature;
    }

    /**
     * Verifies if the remote public key, the signature and the signed text match.
     * <p><b>Ensure that the remote public key is set to the public key of the host with which you are attempting to communicate.</b></p>
     *
     * @param base64EncodedSignature The signature of the signedText
     * @param signedText the signed text
     * @return if the signature match with the signed text and the remote public key
     * @throws ImpossibleConversionException This exception gets thrown if the base64EncodedSignature can not be decoded.
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    public boolean verify(String base64EncodedSignature, String signedText) throws ImpossibleConversionException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        byte[] decodedSignature = CrypterUtil.base64StrToBytes(base64EncodedSignature);
        boolean isCorrectlySigned = Crypter.verify(signedText.getBytes(),decodedSignature, getRemotePublicKey());
        return isCorrectlySigned;
    }


    /**
     * Sets the AES secret key
     * @param secretKey the new secret key
     * @return returns false if the given secret key was null
     *         (if the key is null a random new one gets assigned instead of the provided key)
     */
    public boolean setAESkey(SecretKey secretKey) {
        this.secretKey = secretKey;
        return initAESKey();
    }

    /**
     * Sets the AES secret key
     * @param keyUnit The key unit for the aes key
     */
    public boolean setAESkey(KeyUnit keyUnit) {
        if (keyUnit == null) keyUnit = KeyUnit.DEFAULT;
        this.secretKey = CrypterKey.getAESKey(keyUnit);
        return initAESKey(keyUnit);
    }

    /**
     * Sets the AES secret key to a random new one
     */
    public boolean setAESkey() {
        return setAESkey(KeyUnit.DEFAULT);
    }

    /**
     * Sets the AES-/SecretKey to the given base64 encoded String that has to represent a SecretKey
     * The secret key is used for encryption and decryption from the client/server. Because AES is a symmetric key algorithm the client and server needs the same key.
     * To get the base64 encoded String from the {@link CrypterManager CrypterManager} class
     * @param base64EncodedAESKey The base64 encoded Secret/AES key
     * @return returns true, if the key got successfully set to the remote public key.
     *         If an error occurs (like a {@link NullPointerException NullPointerException}, {@link ImpossibleConversionException ImpossibleConversionException}
     *         or a {@link java.security.spec.InvalidKeySpecException InvalidKeySpecException})
     *         this returns "false"
     */
    public boolean setAESkey(String base64EncodedAESKey) {
        try {
            byte[] decodedAESKey = CrypterUtil.base64StrToBytes(base64EncodedAESKey);
            SecretKey newSecretKey = CrypterKey.getAESkeyFromBytes(decodedAESKey);
            if (newSecretKey == null) return false;
            this.secretKey = newSecretKey;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sets the private key of the key pair
     * @param privateKey The public key that should replace the old one
     * @return returns 'false' if the provided private key is null or other fields of the key pair were null
     *         (the variables with null got replaced to valid ones)
     */
    public boolean setPrivateRSAkey(PrivateKey privateKey) {
        keyPair = new KeyPair(keyPair.getPublic(), privateKey);
        return initKeyPair();
    }

    /**
     * Sets the public key of the key pair
     * @param publicKey The public key that should replace the old one
     * @return returns 'false' if the provided public key is null or other fields of the key pair were null
     *         (the variables with null got replaced to valid ones)
     */
    public boolean setPublicRSAkey(PublicKey publicKey) {
        keyPair = new KeyPair(publicKey, keyPair.getPrivate());
        return initKeyPair();
    }

    /**
     * sets the key pair to the provided one
     * @param keyPair the new key pair
     * @return This returns false if there were some issues with the provided key pair
     *         that got resolved by filling out the keyPair/privateKey/publicKey field
     */
    public boolean setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
        return !initKeyPair();
    }

    /**
     * sets the key pair to a newly generated one
     * @return This returns false if there were some issues with the provided key pair
     *         that got resolved by filling out the keyPair/privateKey/publicKey field
     */
    public boolean setKeyPair() {
        this.keyPair = CrypterKey.getRSAKeyPair();
        return !initKeyPair();
    }

    public KeyPair getKeyPair() {
        initKeyPair();
        return keyPair;
    }

    /**
     * Sets the keypair to a valid keypair instead of null
     * @return if some changes where made to the keypair 'true' gets returned
     */
    private boolean initKeyPair() {
        return initKeyPair(1024);
    }

    /**
     * Sets the keypair to a valid keypair if it or its private/public keys gets changed
     * @param keysize the size of the key must be equal or larger than 512
     * @return If the keyPair gets initialized (or its private/public key) this returns 'true', otherwise this will return 'false'
     */
    private boolean initKeyPair(int keysize) {
        boolean gotChanged = false;
        if (keyPair == null) {
            keyPair = CrypterKey.getRSAKeyPair(keysize);
            gotChanged = true;
        }
        if (keyPair.getPublic() == null) {
            keyPair = new KeyPair(CrypterKey.getRSAKeyPair(keysize).getPublic(), keyPair.getPrivate());
            gotChanged = true;
        }
        if (keyPair.getPrivate() == null) {
            keyPair = new KeyPair(keyPair.getPublic(), CrypterKey.getRSAKeyPair(keysize).getPrivate());
            gotChanged = true;
        }

        return gotChanged;
    }

    /**
     * Sets the secret key to a valid one instead of null
     * @param unit The key size of the key
     * @return If the aes key gets initialized this returns 'true', otherwise this will return 'false'
     */
    private boolean initAESKey(KeyUnit unit) {
        if (unit == null) unit = KeyUnit.DEFAULT;
        if (secretKey == null) {
            secretKey = CrypterKey.getAESKey(unit);
            return true;
        }
        return false;
    }

    /**
     * Gets the Base64 encoded string representation of the public key
     * @return Base64 encoded string representation of the public key
     */
    public String getPublicKey() {
        return keyToBase64(keyPair.getPublic());
    }
    /**
     * Gets the Base64 encoded string representation of the private ley
     * @return Base64 encoded string representation of the private key
     */
    public String getPrivateKey() {
        return keyToBase64(keyPair.getPrivate());
    }
    /**
     * Gets the Base64 encoded string representation of the remote public key
     * @return Base64 encoded string representation of the remote public key
     */
    public String getBase64RemotePublicKey() {
        return keyToBase64(remotePublicKey);
    }

    /**
     * Gets the Base64 encoded string representation of the SecretKey/AESKey
     * @return Base64 encoded string representation of the SecretKey/AESKey
     */
    public String getAESKey() {
        return keyToBase64(secretKey);
    }

    /**
     * Converts the given key into a base64 encoded String
     * @param key The key that should get encoded to a Base64 String
     * @return The base64 encoded String
     */
    private String keyToBase64(Key key) {
        return CrypterUtil.bytesToBase64Str(key.getEncoded());
    }

    /**
     * Sets the initialize vector
     * @param iv the new initialize vector
     */
    public void setIV(byte[] iv) {
        this.iv = CrypterUtil.stringToBase64(
                CrypterUtil.bytesToString(iv)
        );
    }

    /**
     * Sets the initialization vector to the provided base64 encoded string
     * @param base64EncodedIv The base64 encoded initialization vector
     * @return returns 'false' if the given vector was not base64 encoded
     *         returns 'true'  if the given vector was base64 encoded
     */
    public boolean setBase64IV(String base64EncodedIv) {
        boolean valid = CrypterUtil.isBase64(base64EncodedIv);
        if (valid) this.iv = base64EncodedIv;
        return valid;
    }

    /**
     * Gets the base64 encoded IV
     * @return the base64 encoded initialization vector
     */
    public String getBase64IV() {
        return iv;
    }


    /**
     * Returns the set IV
     * @return The initialization vector in a byte array
     * @throws ImpossibleConversionException If the iv is not decodable from base64 to a byte array this exception gets thrown.
     *                                       Encountering this exception is extremely rare when calling this method
     *                                       because there are mechanisms to prevent setting the iv to a non base64 encoded string.
     */
    public byte[] getIV() throws ImpossibleConversionException {
        return CrypterUtil.base64ToString(iv).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Sets the secret key to a valid one instead of null
     * @return If the aes key gets initialized this returns 'true', otherwise this will return 'false'
     */
    private boolean initAESKey() {
        return initAESKey(KeyUnit.DEFAULT);
    }

    /**
     * Returns the AESkey/SecretKey.
     * If the current secretKey attribute is null a new AES key gets generated
     * @return The set SecretKey (or a new one if the AES key was not initialized)
     */
    public SecretKey getAESkey() {
        initAESKey();
        return secretKey;
    }

    /**
     * Sets the remote public key used for encryption.
     * This should be the PublicKey from the other host you are communicating with.
     * <b><u>If the provided public key is null a new random public key gets assigned instead of the given one</u></b>
     * @param remotePublicKey The PublicKey that gets set
     */
    public void setRemotePublicKey(PublicKey remotePublicKey) {
        this.remotePublicKey = remotePublicKey;
        initRemotePublicKey();
    }

    /**
     * Sets the remote public key to the given base64 encoded String that has to represent a PublicKey
     * The remote public key gets used for encryption. It has to be the public key of the client you are communicating with.
     * To get the base64 encoded String from the {@link CrypterManager CrypterManager} class
     * @param base64EncodedRemotePublicKey The base64 encoded Public Key
     * @return returns true, if the key got successfully set to the remote public key.
     *         If an error occurs (like a {@link NullPointerException NullPointerException}, {@link ImpossibleConversionException ImpossibleConversionException}
     *         or a {@link java.security.spec.InvalidKeySpecException InvalidKeySpecException})
     *         this returns "false"
     */
    public boolean setRemotePublicKey(String base64EncodedRemotePublicKey) {
        try {
            byte[] decodedRemotePublicKey = CrypterUtil.base64StrToBytes(base64EncodedRemotePublicKey);
            PublicKey remotePublicKey = CrypterKey.getPublicKeyFromBytes(decodedRemotePublicKey);
            if (remotePublicKey == null) return false;
            this.remotePublicKey = remotePublicKey;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Initializes the remote PublicKey if it is null.
     * This is done, in order to prevent NullPointerExceptions
     * @return If the remote PublicKey gets changed this methode returns true,
     *         otherwise this returns false
     */
    public boolean initRemotePublicKey() {
        initKeyPair();
        if (remotePublicKey == null) {
            this.remotePublicKey = CrypterKey.getRSAKeyPair().getPublic();
            return true;
        } else {
            return false;
        }
    }

    /**
     * returns the public key that gets used for encryption.
     * The remote public key should be the key from the host you're communicating with.
     * @return The remote public key
     */
    public PublicKey getRemotePublicKey() {
        return remotePublicKey;
    }
}
