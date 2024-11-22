package ppl.server.iam.authn.authentication.password;

import org.springframework.security.crypto.bcrypt.BCrypt;
import ppl.common.utils.enumerate.EnumEncoder;
import ppl.common.utils.enumerate.EnumUtils;

import java.util.Arrays;
import java.util.Base64;

public class BcryptClientPasswordEncoder implements ClientPasswordEncoder {

    private final BcryptClientSaltPublisher clientSaltPublisher;

    public BcryptClientPasswordEncoder(String version, int logRounds) {
        this.clientSaltPublisher = new BcryptClientSaltPublisher(version, logRounds);
    }

    public BcryptClientPasswordEncoder(BcryptClientSaltPublisher clientSaltPublisher) {
        this.clientSaltPublisher = clientSaltPublisher;
    }

    @Override
    public ClientSaltPublisher getClientSaltPublisher() {
        return clientSaltPublisher;
    }

    @Override
    public String encode(CharSequence rawPassword, CharSequence username) {
        String salt = clientSaltPublisher.publishTo(username);
        return BCrypt.hashpw(rawPassword.toString(), salt);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }

    public static class BcryptClientSaltPublisher implements ClientSaltPublisher {

        private final String version;
        private final int logRounds;

        public BcryptClientSaltPublisher(String version, int logRounds) {
            EnumUtils.enumOf(BCryptVersion.class, version);
            this.version = version;
            this.logRounds = logRounds;
        }

        public String publishTo(CharSequence username) {
            return String.format("$%s$%d$%s", version, logRounds, Base64.getEncoder().encodeToString(padding(username.toString().getBytes(), ' ', 16)));
        }

        private static byte[] padding(byte[] s, char pad, int size) {
            if (s.length < size) {
                byte[] s1 = new byte[size];
                Arrays.fill(s1, (byte) pad);
                System.arraycopy(s, 0, s1, 0, s.length);
                return s1;
            }
            return s;
        }
    }

    public enum BCryptVersion {
        BC_2A("2a"),
        BC_2Y("2y"),
        BC_2B("2b");

        private final String version;

        BCryptVersion(String version) {
            this.version = version;
        }

        @EnumEncoder
        public String getVersion() {
            return this.version;
        }
    }
}
