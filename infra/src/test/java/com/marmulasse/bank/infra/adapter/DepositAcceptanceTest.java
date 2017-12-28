package com.marmulasse.bank.infra.adapter;

import com.marmulasse.bank.account.commands.handlers.CommandHandler;
import com.marmulasse.bank.account.commands.handlers.DepositCommandHandler;
import com.marmulasse.bank.account.aggregate.Account;
import com.marmulasse.bank.account.aggregate.Amount;
import com.marmulasse.bank.account.commands.MakeDepositCommand;
import com.marmulasse.bank.account.port.AccountRepository;
import com.marmulasse.bank.account.queries.handlers.GetAccountByIdQueryHandler;
import com.marmulasse.bank.account.queries.GetAccountFromId;
import com.marmulasse.bank.account.queries.handlers.QueryHandler;
import com.marmulasse.bank.account.queries.Result;
import com.marmulasse.bank.infra.bus.CommandBus;
import com.marmulasse.bank.infra.bus.QueryBus;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositAcceptanceTest {

    private CommandBus commandBus;
    private AccountRepository accountRepository;
    private QueryBus queryBus;

    @Before
    public void setUp() throws Exception {
        accountRepository = new InMemoryAccountRepository(new HashMap<>());
        CommandHandler<MakeDepositCommand> depositCommandHandler = new DepositCommandHandler(accountRepository);
        QueryHandler<GetAccountFromId, Account> accountByIdQueryHandler = new GetAccountByIdQueryHandler(accountRepository);

        commandBus = new CommandBus(Collections.singletonList(depositCommandHandler));
        queryBus = new QueryBus(Collections.singletonList(accountByIdQueryHandler));
    }


    @Test
    public void should_make_a_deposit() throws Exception {
        Account emptyAccount = Account.empty();
        accountRepository.save(emptyAccount);

        commandBus.dispatch(new MakeDepositCommand(emptyAccount.getAccountId(), Amount.of(1.0)));

        Account updatedAccount = accountRepository.get(emptyAccount.getAccountId()).get();
        assertThat(updatedAccount.getBalance()).isEqualTo(Amount.of(1.0));
    }


    @Test
    public void should_query_an_account() throws Exception {
        Account emptyAccount = Account.empty();
        accountRepository.save(emptyAccount);
        String id = emptyAccount.getAccountId().getValue().toString();

        Result<Account> result = queryBus.ask(new GetAccountFromId(id));
        assertThat(result.getValue()).isEqualTo(emptyAccount);
    }

}
