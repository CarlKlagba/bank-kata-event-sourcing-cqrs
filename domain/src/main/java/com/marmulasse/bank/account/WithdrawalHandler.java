package com.marmulasse.bank.account;

import com.marmulasse.bank.account.aggregate.Account;
import com.marmulasse.bank.account.aggregate.AccountId;
import com.marmulasse.bank.account.aggregate.Amount;
import com.marmulasse.bank.account.exception.AccountNotFound;
import com.marmulasse.bank.account.port.AccountRepository;

public class WithdrawalHandler {
    private AccountRepository accountRepository;

    public WithdrawalHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void makeDeposit(AccountId accountId, Amount amount) throws AccountNotFound {
        Account account = accountRepository.get(accountId).orElseThrow(() -> new AccountNotFound(accountId));
        account.withdraw(amount);
        accountRepository.save(account);
    }
}
