package ppl.server.iam.authn.login.reader;

public class LoginStringDecryptedException extends AuthenticationTokenReaderException {
    public LoginStringDecryptedException(String s) {
        super(s);
    }

    public LoginStringDecryptedException(Throwable t) {
        super(t);
    }
}
