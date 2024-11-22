package ppl.server.iam.authn.login.reader;

public class CorruptRequestException extends AuthenticationTokenReaderException {
    public CorruptRequestException(Throwable t) {
        super(t);
    }
}
