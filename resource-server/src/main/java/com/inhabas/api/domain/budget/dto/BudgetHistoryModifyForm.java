package com.inhabas.api.domain.budget.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetHistoryModifyForm extends BudgetHistoryCreateForm {

  @NotNull @Positive private Integer id;

  public BudgetHistoryModifyForm(
      LocalDateTime dateUsed,
      String title,
      String details,
      Long memberIdReceived,
      String memberNameReceived,
      Integer income,
      Integer outcome,
      Integer historyId) {
    super(dateUsed, title, details, memberIdReceived, memberNameReceived, income, outcome);
    this.id = historyId;
  }
}
