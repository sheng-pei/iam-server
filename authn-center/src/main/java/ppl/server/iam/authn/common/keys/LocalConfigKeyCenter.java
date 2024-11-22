package ppl.server.iam.authn.common.keys;

import ppl.common.utils.security.BCECUtils;
import ppl.common.utils.security.ECUtils;
import ppl.common.utils.string.Strings;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Map;

public class LocalConfigKeyCenter implements KeyCenter {

    private static final String PRI_NAME = "pri";
    private static final String PUB_NAME = "pub";

    private CryptService ec;

    public void setEc(Map<String, String> ec) {
        this.ec = new LocalCryptService(ec.get(PRI_NAME), ec.get(PUB_NAME));
    }

    @Override
    public CryptService ec() {
        return ec;
    }

    private static class LocalCryptService implements CryptService {

        private final String pri;
        private final String pub;
        private transient ECPrivateKey privateKey;
        private transient ECPublicKey publicKey;

        private LocalCryptService(String pri, String pub) {
            if (Strings.isBlank(pri)) {
                throw new IllegalArgumentException("Private key for ec crypt service is required.");
            }
            if (Strings.isBlank(pub)) {
                throw new IllegalArgumentException("Public key for ec crypt service is required.");
            }
            this.pri = pri;
            this.pub = pub;
        }

        @Override
        public String pub() {
            return pub;
        }

        @Override
        public byte[] sign(byte[] data) {
            return ECUtils.sign(getPrivateKey(), data);
        }

        @Override
        public boolean verifySignature(byte[] data, byte[] signature) {
            return ECUtils.verifySignature(getPublicKey(), data, signature);
        }

        @Override
        public byte[] encrypt(byte[] data) {
            return ECUtils.encrypt(getPublicKey(), data);
        }

        @Override
        public byte[] decrypt(byte[] data) {
            return ECUtils.decrypt(getPrivateKey(), data);
        }

        private ECPrivateKey getPrivateKey() {
            if (this.privateKey == null) {
                @SuppressWarnings("deprecation")
                ECPrivateKey privateKey = BCECUtils.privateKey(pri);
                this.privateKey = privateKey;
            }
            return this.privateKey;
        }

        private ECPublicKey getPublicKey() {
            if (this.publicKey == null) {
                @SuppressWarnings("deprecation")
                ECPublicKey publicKey = BCECUtils.publicKey(pub);
                this.publicKey = publicKey;
            }
            return this.publicKey;
        }
    }

}
