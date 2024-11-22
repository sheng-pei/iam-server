package ppl.server.iam.authn.login.reader;

public class ReplayAttackException extends AuthenticationTokenReaderException {
    private final String random;

    public ReplayAttackException(String random) {
        super();
        this.random = random;
    }

    @Override
    public String getMessage() {
        return "Invalid random: " + random + ".";
    }
}
