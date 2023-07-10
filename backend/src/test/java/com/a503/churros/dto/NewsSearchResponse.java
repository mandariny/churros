package com.a503.churros.dto;

import com.a503.churros.dto.news.NewsDocumentationDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsSearchResponse {
  @JsonProperty("content")
  private List<NewsDocumentationDTO> content;

}