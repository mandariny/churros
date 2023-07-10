package com.a503.churros.entity.scrap;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ScrapedArticle")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScrapedArticle {
    @Id
    @Column(name = "scrap_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "scrapbook_idx")
    private long scrapbookIdx;

    @Column(name = "article_idx")
    private long articleIdx;

}
