package com.a503.churros.service.scrap;

import com.a503.churros.dto.scrap.ScrapFolderDTO;

import java.util.List;

public interface ScrapService {
    List<ScrapFolderDTO> getFolderList(long idx);
    List<ScrapFolderDTO> getFolders(long idx , long articleIdx);
    List<Long> getArticleList(long idx, long userIdx);
    Long insertFolderName(long userIdx , String folderName);
    void changeFolderName(long userIdx , String folderName , long folderIdx);
    void saveArticle(long userIdx , long folderIdx , long articleIdx);
    void deleteScrapArticle(long userIdx, long folderIdx, long articleIdx);
    void deleteFolder(long userIdx , long folderIdx);
}
