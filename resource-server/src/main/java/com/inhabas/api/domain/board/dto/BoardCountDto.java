package com.inhabas.api.domain.board.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardCountDto {

  @NotNull private String menuName;

  @NotNull @PositiveOrZero private Integer count;

  @Builder
  public BoardCountDto(String menuName, Integer count) {
    this.menuName = menuName;
    this.count = count;
  }
}
