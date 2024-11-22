package ppl.server.iam.authn.ticket;

import ppl.server.base.webmvc.response.r.MessageParameter;

public class TooManyTicketGeneratedException extends RuntimeException implements MessageParameter {

    private final int minutes;

    public TooManyTicketGeneratedException(String message, int minutes) {
        super(message);
        this.minutes = minutes;
    }

    @Override
    public Object[] params() {
        return new Object[]{minutes};
    }
}
