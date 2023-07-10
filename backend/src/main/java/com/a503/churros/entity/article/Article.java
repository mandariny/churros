package com.a503.churros.entity.article;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import javax.validation.valueextraction.UnwrapByDefault;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "newsCol")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Article {

    @Id
    private String id;
    @Field("_idx")
    private long idx;
    private String title;
    private String description;
    private String link;
    @Field("img_src")
    private String imgSrc;
    @Field("publish_date")
    private String publishDate;
}
