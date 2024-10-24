package com.cjrequena.sample.mapper;

import com.cjrequena.eventstore.sample.domain.event.Event;
import com.cjrequena.eventstore.sample.entity.EventEntity;
import com.cjrequena.sample.common.util.JsonUtil;
import com.cjrequena.sample.domain.event.AccountCreatedEvent;
import com.cjrequena.sample.domain.event.AccountCreditedEvent;
import com.cjrequena.sample.domain.event.AccountDebitedEvent;
import com.cjrequena.sample.vo.AccountVO;
import com.cjrequena.sample.vo.CreditVO;
import com.cjrequena.sample.vo.DebitVO;
import com.cjrequena.sample.vo.EventExtensionVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface EventMapper {

  Logger log = LoggerFactory.getLogger(EventMapper.class);

  EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

  //@Mapping(source = "aggregateId", target = "aggregateId")
  EventEntity toEventEntity(AccountCreatedEvent event);

  //@Mapping(source = "aggregateId", target = "aggregateId")
  EventEntity toEventEntity(AccountCreditedEvent event);

  //    @Mapping(source = "aggregateId", target = "aggregateId")
  EventEntity toEventEntity(AccountDebitedEvent event);

  AccountCreatedEvent toAccountCreatedEvent(EventEntity eventEntity);

  AccountCreditedEvent toAccountCreditedEvent(EventEntity eventEntity);

  AccountDebitedEvent toAccountDebitedEvent(EventEntity eventEntity);

  // Custom mapping method for converting String to EventExtensionVO
  default EventExtensionVO mapToEventExtensionVO(String value) {
    try {
      return JsonUtil.jsonStringToObject(value, EventExtensionVO.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to map JSON string to EventExtensionVO: {}", value, e);
      return null; // Or throw a custom exception based on your requirements
    }
  }

  // You can add another mapping method if you need to go from EventExtensionVO to String
  default String map(EventExtensionVO value) {
    try {
      return JsonUtil.objectToJsonString(value);
    } catch (JsonProcessingException e) {
      log.error("Failed to map EventExtensionVO to JSON string: {}", value, e);
      return null; // Or throw a custom exception based on your requirements
    }
  }

  // Custom mapping method for converting String to AccountVO
  default AccountVO mapToAccountVO(String value) {
    try {
      return JsonUtil.jsonStringToObject(value, AccountVO.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to map JSON string to AccountVO: {}", value, e);
      return null; // Or throw a custom exception based on your requirements
    }
  }

  // You can add another mapping method if you need to go from AccountVO to String
  default String map(AccountVO value) {
    try {
      return JsonUtil.objectToJsonString(value);
    } catch (JsonProcessingException e) {
      log.error("Failed to map AccountVO to JSON string: {}", value, e);
      return null; // Or throw a custom exception based on your requirements
    }
  }

  // Custom mapping method for converting String to CreditVO
  default CreditVO mapToCreditVO(String value) {
    try {
      return JsonUtil.jsonStringToObject(value, CreditVO.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to map JSON string to CreditVO: {}", value, e);
      return null; // Or throw a custom exception based on your requirements
    }
  }

  // You can add another mapping method if you need to go from CreditVO to String
  default String map(CreditVO value) {
    try {
      return JsonUtil.objectToJsonString(value);
    } catch (JsonProcessingException e) {
      log.error("Failed to map CreditVO to JSON string: {}", value, e);
      return null; // Or throw a custom exception based on your requirements
    }
  }

  // Custom mapping method for converting String to DebitVO
  default DebitVO mapToDebitVO(String value) {
    try {
      return JsonUtil.jsonStringToObject(value, DebitVO.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to map JSON string to DebitVO: {}", value, e);
      return null; // Or throw a custom exception based on your requirements
    }
  }

  // You can add another mapping method if you need to go from DebitVO to String
  default String map(DebitVO value) {
    try {
      return JsonUtil.objectToJsonString(value);
    } catch (JsonProcessingException e) {
      log.error("Failed to map DebitVO to JSON string: {}", value, e);
      return null; // Or throw a custom exception based on your requirements
    }
  }

  // New method to map a List of EventEntity to a List of Event
  default List<Event> toEventList(List<EventEntity> eventEntities) {
    return eventEntities.stream()
      .map(this::toEvent)  // Call the helper method for individual mapping
      .collect(Collectors.toList());
  }

  // Helper method to map a single EventEntity to an Event (AccountCreatedEvent, AccountCreditedEvent, etc.)
  default Event toEvent(EventEntity eventEntity) {
    // Assuming EventEntity has a type field or some kind of discriminator
    switch (eventEntity.getEventType()) {
      case "ACCOUNT_CREATED_EVENT":
        return toAccountCreatedEvent(eventEntity);
      case "ACCOUNT_CREDITED_EVENT":
        return toAccountCreditedEvent(eventEntity);
      case "ACCOUNT_DEBITED_EVENT":
        return toAccountDebitedEvent(eventEntity);
      default:
        log.error("Unknown event type: {}", eventEntity.getEventType());
        return null;  // Or throw an exception
    }
  }

}
