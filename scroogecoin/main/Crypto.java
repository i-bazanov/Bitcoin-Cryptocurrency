package scroogecoin.main;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class Crypto {

    /**
     * @return true is {@code signature} is a valid digital signature of {@code message} under the
     *         key {@code pubKey}. Internally, this uses RSA signature, but the student does not
     *         have to deal with any of the implementation details of the specific signature
     *         algorithm
     */
    public static boolean verifySignature(PublicKey pubKey, byte[] message, byte[] signature) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");   //NoSuchAlgorithmException e
            sig.initVerify(pubKey);                                      //InvalidKeyException e
            sig.update(message);                                         //SignatureException e
            return sig.verify(signature);
        } catch (Exception e) {
            // add some error logger Logger.e(e, error when trying verify signature)
            e.printStackTrace();
        }
        return false;
    }
}
