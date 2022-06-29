package jacobitus.token;

import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;

public class ExternalSignatureLocal implements IExternalSignature {
    private final PrivateKey privateKey;
    private final String provider;

    public ExternalSignatureLocal(PrivateKey privateKey, String provider) {
        this.privateKey = privateKey;
        this.provider = provider;
    }

    @Override
    public String getHashAlgorithm() {
        return DigestAlgorithms.getDigest(DigestAlgorithms.getAllowedDigest("SHA256"));
    }

    @Override
    public synchronized String getEncryptionAlgorithm() {
        return privateKey.getAlgorithm();
    }

    @Override
    public synchronized byte[] sign(byte[] sh) throws GeneralSecurityException {
        String signMode = getHashAlgorithm();
        signMode += "with" + privateKey.getAlgorithm();
        Signature signature;
        if (provider.equals("PKCS12")) {
            signature = Signature.getInstance(signMode);
        } else {
            signature = Signature.getInstance(signMode, provider);
        }
        signature.initSign(privateKey);
        signature.update(sh);
        return signature.sign();
    }
}
