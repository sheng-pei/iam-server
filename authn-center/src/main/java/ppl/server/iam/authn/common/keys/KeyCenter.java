package ppl.server.iam.authn.common.keys;

public interface KeyCenter {
    interface CryptService {
        String pub();

        byte[] sign(byte[] data);

        boolean verifySignature(byte[] data, byte[] signature);

        byte[] encrypt(byte[] data);

        byte[] decrypt(byte[] data);
    }

    CryptService ec();
}
