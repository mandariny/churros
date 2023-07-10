package com.a503.churros.dto.scrap;

import lombok.Getter;

@Getter
public class ScrapArticleRequestDTO {
    private String folderName;
    private Long folderIdx;
    private Long articleIdx;
    private Boolean scrapped;

}
