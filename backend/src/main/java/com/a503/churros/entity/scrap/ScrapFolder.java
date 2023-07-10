package com.a503.churros.entity.scrap;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ScrapFolder")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScrapFolder {
    @Id
    @Column(name = "ScrapFolder_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_idx")
    private long userIdx;

    @Column(name = "scrap_folder_name")
    private String folderName;

}
