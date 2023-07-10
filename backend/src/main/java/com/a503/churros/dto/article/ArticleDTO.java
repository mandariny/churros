package com.a503.churros.dto.article;

import com.a503.churros.entity.article.Article;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ArticleDTO {

    private long idx;

    private String title;

    private String description;

    private String url;

    private String imgUrl;

    private String date;

    private boolean like;


    public ArticleDTO of(Article arti){
        return ArticleDTO.builder()
                .idx(arti.getIdx())
                .title(arti.getTitle())
                .description(arti.getDescription())
                .url(arti.getLink())
                .imgUrl(arti.getImgSrc())
                .date(arti.getPublishDate())
                .like(false)
                .build();
    }
}