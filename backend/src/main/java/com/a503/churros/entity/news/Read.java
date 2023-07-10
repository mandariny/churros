package com.a503.churros.entity.news;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ReadArticle")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Read {

    @Id
    @Column(name = "read_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_idx")
    private long userIdx;

    @Column(name = "article_idx")
    private long articleIdx;

    @Column(name = "read_date")
    private LocalDateTime readDate;

    @Column(name = "valid_date")
    private LocalDateTime validDate;
}
