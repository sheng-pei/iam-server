package ppl.server.iam.authn.authentication.password;

import ppl.common.utils.security.SecureRandom;

public class DefaultPasswordGenerator implements PasswordGenerator {

    private static final byte[] DEFAULT_AVAILABLE_PASSWORD_CHARACTERS = (
            "abcdefghijklmnopqrstuvwxyz" +
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "0123456789" +
                    "!@#$%^&*()-_=+[]\\|{}';:\",.<>/?"
    ).getBytes();
    private static final int DEFAULT_LENGTH = 20;

    private final byte[] availablePasswordCharacters;
    private final int length;

    public DefaultPasswordGenerator() {
        this.availablePasswordCharacters = DEFAULT_AVAILABLE_PASSWORD_CHARACTERS;
        this.length = DEFAULT_LENGTH;
    }

    public DefaultPasswordGenerator(String passwordCharacters, int length) {
        byte[] bytes = passwordCharacters.getBytes();
        if (bytes.length > 255) {
            throw new IllegalArgumentException("The number of available password characters must not be exceed 255.");
        }
        this.availablePasswordCharacters = bytes;
        this.length = length;
    }

    @Override
    public String generate() {
        byte[] bytes = SecureRandom.defStrong().nextBytes(length);
        byte[] ret = new byte[length];
        for (int i = 0; i < bytes.length; i++) {
            int index = bytes[i] + 128;
            ret[i] = availablePasswordCharacters[index % availablePasswordCharacters.length];
        }
        return new String(ret);
    }

}
