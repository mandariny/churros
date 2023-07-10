package com.a503.churros.dto.scrap;

import com.a503.churros.entity.scrap.ScrapFolder;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScrapFolderDTO {

    private long folderIdx;

    private String folderName;

    private boolean isScrapped;

    public static ScrapFolderDTO of(ScrapFolder sf){
        return ScrapFolderDTO.builder()
                .folderIdx(sf.getId())
                .folderName(sf.getFolderName())
                .build();
    }
    public static ScrapFolderDTO of(ScrapFolder sf , boolean t){
        return ScrapFolderDTO.builder()
                .folderIdx(sf.getId())
                .folderName(sf.getFolderName())
                .isScrapped(t)
                .build();
    }
}
