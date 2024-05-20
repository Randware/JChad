package net.jchad.client.model.client.testing;

import com.google.gson.Gson;
import net.jchad.shared.cryptography.Crypter;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.cryptography.ImpossibleConversionException;
import net.jchad.shared.cryptography.keys.CrypterKey;
import net.jchad.shared.networking.packets.encryption.AESencryptionKeysPacket;
import net.jchad.shared.networking.packets.encryption.RSAkeyPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

// Temporary client used for testing
public class ClientSampleCode {
    public static void main(String[] args) {
        /*
        Private: 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
Public: MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzYjE0Cx46blikHJYs2wKXdWatMTEomVIwq+gmrvKuMr32yvm+ZBJYFHTOF+wN0FWs5dsenjJEiKHl0q23AlWCbgvDo3iEHxuXHl5NkAdMKNEynHYQ+dy5sCMG4p4+7xaKIUjswtUAxkrwkaL7Ug61Da3xdfjKE6c/Kfj2qeE7igs12Ow63KlgtX/x9I5YJw0AtvXpItHqy1fqM/1vGu+Uno16ymkgr9HzBgsVpAUbLafheqRF1z97ca+lZ6o1ELZIbG9EVPVDl0zlN+nfzvsdlaR6WU6JNS0Q8H4h3vJayrorXkcLaJi43zNczhmCzuit9C3JCiBrN+NsGrETx/SzGV6qUTHi+8S3LgmrhhWfleZAhqiSjKRu5p43haKcJ4b0ueZS3MrdJqTH9xLE1xjETAfndSSPRYVt3mOW2bXbntAe6d1LYN73dAB5FILhFftBhwAzOQvR88Kqf5OLBC2lCSJNxc/2PdHgUWkwRAEzPhrvz5p1xFB+3Er5Pg60ZF3FpcIzInAU8mrLGTZ88SoaEEWhb4XpwzyhwpeAqvYf5EW2DxQqxqK3Cidz9Ahc2RFmhNDxVZ4yB8fHvPj2SkMckHDgD0qd0gfaTerfVBoQc7zITA0KSD4UbF3mN+q4O53/+cYHvY3ZVBuECfHYDhO6eyyGTECdF8PACTGTQ2ILI0CAwEAAQ==
         */
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 13814);
            CrypterManager crypterManager = new CrypterManager();
            crypterManager.setPublicRSAkey(CrypterKey.getPublicKeyFromBytes(Base64.getDecoder().decode("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzYjE0Cx46blikHJYs2wKXdWatMTEomVIwq+gmrvKuMr32yvm+ZBJYFHTOF+wN0FWs5dsenjJEiKHl0q23AlWCbgvDo3iEHxuXHl5NkAdMKNEynHYQ+dy5sCMG4p4+7xaKIUjswtUAxkrwkaL7Ug61Da3xdfjKE6c/Kfj2qeE7igs12Ow63KlgtX/x9I5YJw0AtvXpItHqy1fqM/1vGu+Uno16ymkgr9HzBgsVpAUbLafheqRF1z97ca+lZ6o1ELZIbG9EVPVDl0zlN+nfzvsdlaR6WU6JNS0Q8H4h3vJayrorXkcLaJi43zNczhmCzuit9C3JCiBrN+NsGrETx/SzGV6qUTHi+8S3LgmrhhWfleZAhqiSjKRu5p43haKcJ4b0ueZS3MrdJqTH9xLE1xjETAfndSSPRYVt3mOW2bXbntAe6d1LYN73dAB5FILhFftBhwAzOQvR88Kqf5OLBC2lCSJNxc/2PdHgUWkwRAEzPhrvz5p1xFB+3Er5Pg60ZF3FpcIzInAU8mrLGTZ88SoaEEWhb4XpwzyhwpeAqvYf5EW2DxQqxqK3Cidz9Ahc2RFmhNDxVZ4yB8fHvPj2SkMckHDgD0qd0gfaTerfVBoQc7zITA0KSD4UbF3mN+q4O53/+cYHvY3ZVBuECfHYDhO6eyyGTECdF8PACTGTQ2ILI0CAwEAAQ==")));
            crypterManager.setPrivateRSAkey(CrypterKey.getPrivateKeyFromBytes(Base64.getDecoder().decode("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")));
            Gson gson = new Gson();



            // Obtain input and output streams
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();


            // Wrap the streams with readers and writers for easier communication
            PrintWriter out = new PrintWriter(outputStream, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            // Start a thread for listening to server responses
            Thread listenerThread = new Thread(() -> {
                try {
                    String response;
                    String com_key = null;
                    String com_iv = null;
                    boolean keysExchanged = false;
                    int iteration = 0;
                    while ((response = in.readLine()) != null) {
                        if (keysExchanged) {
                            try {
                                System.out.println("Server response: " + crypterManager.decryptAES(response));
                            } catch (InvalidAlgorithmParameterException e) {
                                throw new RuntimeException(e);
                            } catch (NoSuchPaddingException e) {
                                throw new RuntimeException(e);
                            } catch (IllegalBlockSizeException e) {
                                throw new RuntimeException(e);
                            } catch (NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            } catch (BadPaddingException e) {
                                throw new RuntimeException(e);
                            } catch (InvalidKeyException e) {
                                throw new RuntimeException(e);
                            } catch (ImpossibleConversionException e) {
                                System.out.println(response);
                                throw new RuntimeException(e);
                            }
                        } else {
                            if (iteration <= 1) {
                                iteration++;
                            } else if (iteration == 2) {
                                try {
                                    AESencryptionKeysPacket aes = gson.fromJson(crypterManager.decryptRSA(response), AESencryptionKeysPacket.class);
                                    System.out.println(aes.toJSON());
                                    crypterManager.setBase64IV(aes.getCommunication_initialization_vector());
                                    crypterManager.setAESkey(aes.getCommunication_key());
                                    keysExchanged = true;
                                } catch (NoSuchPaddingException e) {
                                    throw new RuntimeException(e);
                                } catch (IllegalBlockSizeException e) {
                                    throw new RuntimeException(e);
                                } catch (NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                } catch (BadPaddingException e) {
                                    throw new RuntimeException(e);
                                } catch (InvalidKeyException e) {
                                    throw new RuntimeException(e);
                                } catch (ImpossibleConversionException e) {
                                    throw new RuntimeException(e);
                                }
                                iteration++;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            listenerThread.start();

            // Scanner for user input
            Scanner scanner = new Scanner(System.in);

            out.println("{\"packet_type\":\"RSA_KEY_EXCHANGE\",\"public_key\":\"MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzYjE0Cx46blikHJYs2wKXdWatMTEomVIwq+gmrvKuMr32yvm+ZBJYFHTOF+wN0FWs5dsenjJEiKHl0q23AlWCbgvDo3iEHxuXHl5NkAdMKNEynHYQ+dy5sCMG4p4+7xaKIUjswtUAxkrwkaL7Ug61Da3xdfjKE6c/Kfj2qeE7igs12Ow63KlgtX/x9I5YJw0AtvXpItHqy1fqM/1vGu+Uno16ymkgr9HzBgsVpAUbLafheqRF1z97ca+lZ6o1ELZIbG9EVPVDl0zlN+nfzvsdlaR6WU6JNS0Q8H4h3vJayrorXkcLaJi43zNczhmCzuit9C3JCiBrN+NsGrETx/SzGV6qUTHi+8S3LgmrhhWfleZAhqiSjKRu5p43haKcJ4b0ueZS3MrdJqTH9xLE1xjETAfndSSPRYVt3mOW2bXbntAe6d1LYN73dAB5FILhFftBhwAzOQvR88Kqf5OLBC2lCSJNxc/2PdHgUWkwRAEzPhrvz5p1xFB+3Er5Pg60ZF3FpcIzInAU8mrLGTZ88SoaEEWhb4XpwzyhwpeAqvYf5EW2DxQqxqK3Cidz9Ahc2RFmhNDxVZ4yB8fHvPj2SkMckHDgD0qd0gfaTerfVBoQc7zITA0KSD4UbF3mN+q4O53/+cYHvY3ZVBuECfHYDhO6eyyGTECdF8PACTGTQ2ILI0CAwEAAQ\\u003d\\u003d\"}");
            Thread.sleep(1000);
            out.println(crypterManager.encryptAES("{\"password\":\"n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg\\u003d\",\"packet_type\":\"PASSWORD\",\"password_packet_type\":\"PASSWORD_RESPONSE\"}"));
            String randChars= "abcdefghijklmnopQRSTUVWXYZ";
            String username = "";
            for (int i = 0; 6 > i ; i++) {
                username += randChars.charAt(new Random().nextInt(0, randChars.length()));
            }

            out.println(crypterManager.encryptAES("{\"username\":\"" + username + "\",\"packet_type\":\"USERNAME\"}"));
            // Start sending user input to the server

            while (true) {

                    out.println(crypterManager.encryptAES(scanner.nextLine()));
                    out.flush();


            }

        } catch (IOException  e) {
            e.printStackTrace();
        } catch (ImpossibleConversionException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
