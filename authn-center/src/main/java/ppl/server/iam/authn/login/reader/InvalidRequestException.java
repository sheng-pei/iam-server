package ppl.server.iam.authn.login.reader;

public class InvalidRequestException extends AuthenticationTokenReaderException {
    public InvalidRequestException(String s, Throwable t) {
        super(s, t);
    }

    public InvalidRequestException(Throwable t) {
        super(t);
    }
}
