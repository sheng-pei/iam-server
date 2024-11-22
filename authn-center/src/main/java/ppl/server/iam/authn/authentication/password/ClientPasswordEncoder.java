package ppl.server.iam.authn.authentication.password;

public interface ClientPasswordEncoder {
    ClientSaltPublisher getClientSaltPublisher();
    String encode(CharSequence rawPassword, CharSequence username);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
