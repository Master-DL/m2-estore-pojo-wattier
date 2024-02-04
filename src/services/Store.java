package services;

import core.Client;
import data.Cart;
import data.Order;
import estorePojo.exceptions.InsufficientBalanceException;
import estorePojo.exceptions.InvalidCartException;
import estorePojo.exceptions.UnknownAccountException;
import estorePojo.exceptions.UnknownItemException;

public interface Store {
    /**
     * @param item a given item
     * @return the price of a given item
     * @throws UnknownItemException
     */
    double getPrice(Object item) throws UnknownItemException;

    /**
     * @param item a given item
     * @param qty  a given quantity
     * @return true if the given quantity of the given item is available
     * directly from the store
     * i.e. without having to re-order it from the provider
     */
    boolean isAvailable(Object item, int qty)
            throws UnknownItemException;

    /**
     * Add an item to a cart.
     * If the cart does not exist yet, create a new one.
     * This method is called for each item one wants to add to the cart.
     *
     * @param cart   a previously created cart or null
     * @param client
     * @param item
     * @param qty
     * @return Implementation dependant.
     * Either a new cart at each call or the same cart updated.
     * @throws UnknownItemException
     * @throws {MismatchClientCartException} if the given client does not own the given cart
     */
    Cart addItemToCart(
            Cart cart,
            Client client,
            Object item,
            int qty)
            throws UnknownItemException, InvalidCartException;

    /**
     * Once all the items have been added to the cart,
     * this method finish make the payment
     *
     * @param cart
     * @param address
     * @param bankAccountRef
     * @return the order
     * @throws UnknownItemException
     */
    Order pay(Cart cart, String address, String bankAccountRef)
            throws
            InvalidCartException, UnknownItemException,
            InsufficientBalanceException, UnknownAccountException;

    /**
     * Used by a client to order an item.
     * The whole process of ordering is encapsulated by this method.
     * If several items need to be ordered, this method needs to be
     * called several times, but the items will appear in separate orders.
     *
     * @param client
     * @param item
     * @param qty
     * @param address
     * @param bankAccountRef
     * @return the order
     * @throws UnknownItemException
     * @throws InsufficientBalanceException
     * @throws UnknownAccountException
     */
    Order oneShotOrder(
            Client client,
            Object item,
            int qty,
            String address,
            String bankAccountRef
    )
            throws
            UnknownItemException,
            InsufficientBalanceException, UnknownAccountException;
}
