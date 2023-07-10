package com.a503.churros.entity.news;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "DisLikedArticle")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DisLike {

    @Id
    @Column(name = "likes_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_idx")
    private long userIdx;

    @Column(name = "article_idx")
    private long articleIdx;

}
