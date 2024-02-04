package services;

import core.Client;
import data.Cart;
import data.ItemInStock;
import data.Order;
import estorePojo.exceptions.InsufficientBalanceException;
import estorePojo.exceptions.InvalidCartException;
import estorePojo.exceptions.UnknownAccountException;
import estorePojo.exceptions.UnknownItemException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StoreImpl implements Store {

    private final Provider provider;
    private final Bank bank;
    /**
     * A map of emitted orders.
     * keys = order keys as Integers
     * values = Order instances
     */
    private final Map<Integer, Order> orders = new HashMap<>();
    /**
     * A map of items available in the stock of the store.
     * keys = the references of the items as Objects
     * values = ItemInStock instances
     */
    private final Map<Object, ItemInStock> itemsInStock = new HashMap<>();

    /**
     * Constructs a new StoreImpl
     */
    public StoreImpl(Provider prov, Bank bk) {
        provider = prov;
        bank = bk;
    }

    @Override
    public double getPrice(Object item) throws UnknownItemException {
        return provider.getPrice(item);
    }

    @Override
    public boolean isAvailable(Object item, int qty)
            throws UnknownItemException {

        if (!itemsInStock.containsKey(item))
            throw new UnknownItemException(
                    "Item " + item +
                            " does not correspond to any known reference");

        ItemInStock iis = itemsInStock.get(item);
        boolean isAvailable = (iis.getQuantity() >= qty);

        return isAvailable;
    }

    @Override
    public Cart addItemToCart(
            Cart cart,
            Client client,
            Object item,
            int qty)
            throws UnknownItemException, InvalidCartException {

        if (cart == null) {
            // If no cart is provided, create a new one
            cart = new Cart(client);
        } else {
            if (client != cart.getClient())
                throw new InvalidCartException(
                        "Cart " + cart + " does not belong to " + client);
        }

        cart.addItem(item, qty);

        return cart;
    }

    @Override
    public Order pay(Cart cart, String address, String bankAccountRef)
            throws
            InvalidCartException, UnknownItemException,
            InsufficientBalanceException, UnknownAccountException {

        if (cart == null)
            throw new InvalidCartException("Cart shouldn't be null");

        // Create a new order
        Order order = new Order(cart.getClient(), address, bankAccountRef);
        orders.put(order.getKey(), order);

        // Order all the items of the cart
        Set entries = cart.getItems().entrySet();
        for (Iterator iter = entries.iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object item = entry.getKey();
            int qty = ((Integer) entry.getValue()).intValue();

            treatOrder(order, item, qty);
        }
        double amount = order.computeAmount();

        // Make the payment
        // Throws InsuffisiantBalanceException if the client account is
        // not sufficiently balanced
        bank.transfert(bankAccountRef, toString(), amount);

        return order;
    }

    @Override
    public Order oneShotOrder(
            Client client,
            Object item,
            int qty,
            String address,
            String bankAccountRef
    )
            throws
            UnknownItemException,
            InsufficientBalanceException, UnknownAccountException {

        // Create a new order
        Order order = new Order(client, address, bankAccountRef);
        orders.put(order.getKey(), order);

        // Treat the item ordered
        treatOrder(order, item, qty);
        double amount = order.computeAmount();

        // Make the payment
        // Throws InsuffisiantBalanceException if the client account is
        // not sufficiently balanced
        bank.transfert(bankAccountRef, toString(), amount);

        return order;
    }

    /**
     * Treat an item ordered by a client and update the corresponding order.
     *
     * @param order
     * @param item
     * @param qty
     * @return
     * @throws UnknownItemException
     * @throws InsufficientBalanceException
     * @throws UnknownAccountException
     */
    private void treatOrder(Order order, Object item, int qty)
            throws UnknownItemException {

        // The number of additional item to order
        // in case we need to place an order to the provider
        final int more = 10;

        // The price of the ordered item
        // Throws UnknownItemException if the item does not exist
        final double price = provider.getPrice(item);

        final double totalAmount = price * qty;

        // The delay (in hours) for delivering the order
        // By default, it takes 2 hours to ship items from the stock
        // This delay increases if an order is to be placed to the provider
        int delay = 2;

        // Check whether the item is available in the stock
        // If not, place an order for it to the provider
        ItemInStock iis = itemsInStock.get(item);
        if (iis == null) {
            int quantity = qty + more;
            delay += provider.order(this, item, quantity);
            ItemInStock newItem = new ItemInStock(item, more, price, provider);
            itemsInStock.put(item, newItem);
        } else {
            // The item is in the stock
            // Check whether there is a sufficient number of them
            // to match the order
            if (iis.getQuantity() >= qty) {
                iis.changeQuantity(qty);
            } else {
                // An order to the provider needs to be issued
                int quantity = qty + more;
                delay += provider.order(this, item, quantity);
                iis.changeQuantity(more);
            }
        }

        // Update the order
        order.addItem(item, qty, price);
        order.setDelay(delay);
    }

    // -----------------------------------------------------
    // Other methods
    // -----------------------------------------------------

    public String toString() {
        return "E-Store";
    }
}
