package ppl.server.iam.authn.authentication.password;

/**
 * Publish salt to user for using on client regardless of whether the username exists or not.
 * It is the best case that each salt published to the same username is the same.
 */
public interface ClientSaltPublisher {
    String publishTo(CharSequence username);
}
