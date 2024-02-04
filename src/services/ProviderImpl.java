package services;

import estorePojo.exceptions.UnknownItemException;

import java.util.HashMap;
import java.util.Map;

public class ProviderImpl implements Provider {

    private final Map<String, Double> itemPrices = new HashMap<>();

    /**
     * Constructs a new ProviderImpl
     */
    public ProviderImpl() {
        itemPrices.put("CD", 15d);
        itemPrices.put("DVD", 20d);
    }

    @Override
    public double getPrice(Object item) throws UnknownItemException {

        if (!itemPrices.containsKey(item))
            throw new UnknownItemException("Item " + item + " is not an item delivered by this provider.");

        Double price = itemPrices.get(item);
        return price.doubleValue();
    }

    @Override
    public int order(Store store, Object item, int qty) throws UnknownItemException {

        if (!itemPrices.containsKey(item))
            throw new UnknownItemException("Item " + item + " is not an item delivered by this provider.");

        // Actually the production process is quite chaotic
        // We only know that the production a random number of hours!!
        double r = Math.random() * 10 * qty;
        return (int) r;
    }

}
