package services;

import estorePojo.exceptions.UnknownItemException;

public interface Provider {
    /**
     * Get the price of an item provided by this provider.
     *
     * @param item
     * @return
     */
    double getPrice(Object item) throws UnknownItemException;

    /**
     * Emit an order for items. The provider returns the delay for delivering the
     * items.
     *
     * @param store the store that emits the order
     * @param item  the item ordered
     * @param qty   the quantity ordered
     * @return the delay (in hours)
     */
    int order(Store store, Object item, int qty) throws UnknownItemException;
}
