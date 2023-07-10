package com.a503.churros.entity.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "news")
@Getter
@AllArgsConstructor
public class NewsDocumentation {

  @Id
  private String id;

  @Field(type = FieldType.Text)
  private String title;

  @Field(type = FieldType.Text)
  private String description;

  @Field(name = "_idx", type = FieldType.Long)
  private Long idx;

  @Field(type = FieldType.Keyword)
  private String link;

  @Field(name = "img_src", type = FieldType.Keyword)
  private String imgSrc;

}