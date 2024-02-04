package estorePojo.exceptions;

import java.io.Serial;

public class UnknownItemException extends Exception {

    @Serial
    private static final long serialVersionUID = -1585427033037517192L;

    private Object item;

    public UnknownItemException(String msg) {
        super(msg);
    }

    @Override
    public String getMessage() {
        return "Item " + item + " is not an item delivered by this provider.";
    }
}
