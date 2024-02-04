package main;

import services.*;
import core.Client;

public class Main {

    public static void main(String[] args) {
        Provider prov = new ProviderImpl();
        Bank bank = new BankImpl();
        Store store = new StoreImpl(prov, bank);
        Client cl = new Client(store);

        cl.run();

    }

}
