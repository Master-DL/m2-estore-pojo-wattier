package estorePojo.exceptions;

import java.io.Serial;

public class InvalidCartException extends Exception {

    @Serial
    private static final long serialVersionUID = -6561524790558683191L;

    public InvalidCartException(String msg) {
        super(msg);
    }

}
