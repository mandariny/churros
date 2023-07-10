package com.a503.churros.dto.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class NewsSearchRequest {
  private String text;
  private int page;

  private int size;


}
