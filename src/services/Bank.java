package services;

import estorePojo.exceptions.InsufficientBalanceException;
import estorePojo.exceptions.UnknownAccountException;

public interface Bank {
    void transfert(String from, String to, double amount)
            throws InsufficientBalanceException, UnknownAccountException;
}
