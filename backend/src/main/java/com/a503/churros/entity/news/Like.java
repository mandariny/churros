package com.a503.churros.entity.news;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LikedArticle")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Like {

    @Id
    @Column(name = "likes_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_idx")
    private long userIdx;

    @Column(name = "article_idx")
    private long articleIdx;

}
