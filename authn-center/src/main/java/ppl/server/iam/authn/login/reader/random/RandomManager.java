package ppl.server.iam.authn.login.reader.random;

public interface RandomManager {
    /**
     * Create a new random. The new random is valid for a short period of time.
     *
     * @return new random
     */
    String create(String prefix);

    /**
     * Whether what result checking is, random is cleaned immediately.
     */
    boolean check(String random);
}
