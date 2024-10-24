package com.cjrequena.sample.component;

import com.cjrequena.eventstore.sample.domain.aggregate.Aggregate;
import com.cjrequena.eventstore.sample.domain.command.Command;
import com.cjrequena.eventstore.sample.service.EventStoreService;
import com.cjrequena.sample.domain.command.CreateAccountCommand;
import com.cjrequena.sample.exception.service.AccountBalanceServiceException;
import com.cjrequena.sample.vo.AccountVO;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class CreateAccountCommandHandler implements CommandHandler<CreateAccountCommand> {

  private final EventStoreService eventStoreService;


  @Override
  public void handle(Command command, Aggregate aggregate) {
    log.trace("Handling {} with command {}", CreateAccountCommand.class.getSimpleName(), command);
    final AccountVO accountVO = ((CreateAccountCommand) command).getAccountVO();
    if (accountVO.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
      throw new AccountBalanceServiceException("Invalid account balance: The account balance must be greater than zero.");
    }
    aggregate.applyCommand(command);
    eventStoreService.saveAggregate(aggregate);
    aggregate.markUnconfirmedEventsAsConfirmed();
  }

  @Nonnull
  @Override
  public Class<CreateAccountCommand> getCommandType() {
    return CreateAccountCommand.class;
  }


}
