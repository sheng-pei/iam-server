package ppl.server.iam.authn.login.reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.session.ForceEagerSessionCreationFilter;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import ppl.common.utils.ArrayUtils;
import ppl.server.base.webmvc.response.writer.Writers;
import ppl.server.iam.authn.common.keys.KeyCenter;
import ppl.server.iam.authn.login.reader.random.LTRandomFilter;
import ppl.server.iam.authn.login.reader.random.RandomManager;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class EncryptedUUPATReader<H extends HttpSecurityBuilder<H>>
        implements UnauthenticatedUsernamePasswordAuthenticationTokenReader<H> {

    private static final String LOGIN_MESSAGE_DELIM = "$";

    private static final List<MediaType> SUPPORTED_MEDIA_TYPES = Arrays.asList(
            MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM);
    private static final HttpMessageConverter<byte[]> MESSAGE_CONVERTER;

    static {
        ByteArrayHttpMessageConverter messageConverter = new ByteArrayHttpMessageConverter();
        messageConverter.setSupportedMediaTypes(SUPPORTED_MEDIA_TYPES);
        MESSAGE_CONVERTER = messageConverter;
    }

    private LTRandomFilter randomFilter;
    private KeyCenter keyCenter;
    private RandomManager randomManager;

    @Autowired
    public void setKeyCenter(KeyCenter keyCenter) {
        this.keyCenter = keyCenter;
    }

    @Autowired
    public void setRandomManager(RandomManager randomManager) {
        if (randomManager != null) {
            LTRandomFilter randomFilter = new LTRandomFilter();
            randomFilter.setRandomManager(randomManager);
            this.randomFilter = randomFilter;
        }
        this.randomManager = randomManager;
    }

    @Override
    public void postProcess(ObjectPostProcessor<Object> processor) {
        if (randomFilter != null) {
            processor.postProcess(randomFilter);
        }
    }

    @Override
    public void addFilters(H builder) {
        if (randomFilter != null) {
            builder.addFilterBefore(randomFilter, ForceEagerSessionCreationFilter.class);
        }
    }

    @Override
    public UsernamePasswordAuthenticationToken readFrom(HttpServletRequest request) {
        byte[] body;
        try {
            body = body(request);
        } catch (IOException e) {
            throw new CorruptRequestException(e);
        } catch (HttpMediaTypeNotSupportedException |
                 HttpMediaTypeNotAcceptableException |
                 HttpMessageNotReadableException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        }

        //解密
        byte[] decryptedBody = keyCenter == null ? body : decrypt(body);
        LoginMessage message;
        try {
            message = parse(new String(decryptedBody, StandardCharsets.ISO_8859_1));
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Valid login strings are " +
                    "{random}${pwd_digest}${username}, random has no '$', pwd_digest has no '$'", e);
        }
        if (randomManager != null) {
            replayPrevent(message);
        }
        return UsernamePasswordAuthenticationToken.unauthenticated(message.getUsername(), message.getPwdDigest());
    }

    private byte[] body(HttpServletRequest request) throws
            IOException,
            HttpMediaTypeNotSupportedException,
            HttpMediaTypeNotAcceptableException,
            HttpMessageNotReadableException {
        HttpInputMessage inputMessage = new ServletServerHttpRequest(request);

        MediaType contentType;
        try {
            contentType = inputMessage.getHeaders().getContentType();
        } catch (InvalidMediaTypeException ex) {
            throw new HttpMediaTypeNotSupportedException(null, SUPPORTED_MEDIA_TYPES, ex.getMessage());
        }

        if (contentType == null) {
            throw new HttpMediaTypeNotAcceptableException(SUPPORTED_MEDIA_TYPES);
        }

        byte[] body = ArrayUtils.zeroByte();
        EmptyBodyCheckingHttpInputMessage message = new EmptyBodyCheckingHttpInputMessage(inputMessage);
        if (MESSAGE_CONVERTER.canRead(byte[].class, contentType)) {
            if (message.hasBody()) {
                body = MESSAGE_CONVERTER.read(byte[].class, message);
            }
        }
        return body;
    }

    private byte[] decrypt(byte[] body) {
        if (keyCenter.ec() == null) {
            throw new LoginStringDecryptedException("Private key not found.");
        }

        try {
            return keyCenter.ec().decrypt(body);
        } catch (Throwable t) {
            throw new LoginStringDecryptedException(t);
        }
    }

    //{random}${pwd_digest}${username}, random has no '$', pwd_digest has no '$'.
    private LoginMessage parse(String msg) {
        String random;
        String pwdDigest;
        String username;
        int start = 0;
        int idx = msg.indexOf(LOGIN_MESSAGE_DELIM, start);
        if (idx >= 0) {
            random = msg.substring(start, idx);
            start = idx + 1;
        } else {
            random = msg;
            start = msg.length();
        }

        idx = msg.indexOf(LOGIN_MESSAGE_DELIM, start);
        if (idx >= 0) {
            pwdDigest = msg.substring(start, idx);
            start = idx + 1;
        } else {
            pwdDigest = msg.substring(start);
            start = msg.length();
        }

        username = msg.substring(start);
        return new LoginMessage(random, pwdDigest, username);
    }

    private void replayPrevent(LoginMessage message) {
        if (!randomManager.check(message.getRandom())) {
            throw new ReplayAttackException(message.getRandom());
        }
    }

    private static class LoginMessage {
        private final String random;
        private final String pwdDigest;
        private final String username;

        public LoginMessage(String random, String pwdDigest, String username) {
            if (random.isEmpty() || pwdDigest.isEmpty() || username.isEmpty()) {
                StringBuilder msg = new StringBuilder("Missing ");
                if (random.isEmpty()) {
                    msg.append("random, ");
                }
                if (pwdDigest.isEmpty()) {
                    msg.append("password digest, ");
                }
                if (username.isEmpty()) {
                    msg.append("username, ");
                }
                msg.setLength(msg.length() - 2);
                throw new IllegalArgumentException(msg.toString());
            }

            this.random = random;
            this.pwdDigest = pwdDigest;
            this.username = username;
        }

        public String getRandom() {
            return random;
        }

        public String getPwdDigest() {
            return pwdDigest;
        }

        public String getUsername() {
            return username;
        }
    }
}
