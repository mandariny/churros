package com.a503.churros.service.scrap;

import com.a503.churros.dto.scrap.ScrapFolderDTO;
import com.a503.churros.entity.scrap.ScrapFolder;
import com.a503.churros.entity.scrap.ScrapedArticle;
import com.a503.churros.global.exception.ScrapFolderInsertException;
import com.a503.churros.repository.scrap.ScrapFolderRepository;
import com.a503.churros.repository.scrap.ScrapedArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final ScrapFolderRepository sfr;
    private final ScrapedArticleRepository sar;

    @Override
    public List<ScrapFolderDTO> getFolderList(long idx) {
        List<ScrapFolder> list = sfr.findByUserIdx(idx).orElse(null);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.stream()
                    .map(m -> ScrapFolderDTO.of(m))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<ScrapFolderDTO> getFolders(long idx, long articleIdx) {
        List<ScrapFolder> list = sfr.findByUserIdx(idx).orElse(null);

        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.stream()
                    .map(m -> {
                        ScrapedArticle sa = sar.findByScrapbookIdxAndArticleIdx(m.getId(), articleIdx).orElse(null);
                        boolean t;
                        ScrapFolderDTO.of(m);
                        if (sa != null) {
                            t = true;
                        } else {
                            t = false;
                        }
                        return ScrapFolderDTO.of(m, t);
                    })
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<Long> getArticleList(long idx, long userIdx) {
        if (sfr.findByIdAndUserIdx(idx, userIdx).orElse(null) == null) {
            return null;
        }
        List<ScrapedArticle> list = sar.findByScrapbookIdx(idx).orElse(null);
        if (list == null || list.size() == 0) {
            return null;
        }
        else{

            List<Long> result = list.stream()
                    .map(m -> m.getArticleIdx())
                    .collect(Collectors.toList());
            Collections.reverse(result);
            return result;

        }
    }

    @Override
    public Long insertFolderName(long userIdx, String folderName) {
        if (sfr.findByFolderNameAndUserIdx(folderName, userIdx).orElse(null) != null) {
            throw new ScrapFolderInsertException("이미 존재하는 폴더입니다");
        }
        ScrapFolder sf = ScrapFolder.builder()
                .folderName(folderName)
                .userIdx(userIdx)
                .build();
        sf = sfr.save(sf);
        return sf.getId();
    }

    @Override
    public void saveArticle(long userIdx, long folderIdx, long articleIdx) {
        ScrapFolder scrapFolder = sfr.findByIdAndUserIdx(folderIdx, userIdx)
                .orElseThrow(() -> new NoSuchElementException("폴더가 존재하지 않습니다"));

        ScrapedArticle scrapedArticle = sar.findByScrapbookIdxAndArticleIdx(folderIdx, articleIdx)
                .orElse(ScrapedArticle.builder()
                        .articleIdx(articleIdx)
                        .scrapbookIdx(folderIdx)
                        .build());

        sar.save(scrapedArticle);
    }

    @Override
    @Transactional
    public void deleteFolder(long userIdx, long folderIdx) {
        if (sfr.findByIdAndUserIdx(folderIdx, userIdx).orElse(null) == null) {
            throw new ScrapFolderInsertException("폴더가 존재하지 않습니다.");
        }
        sfr.deleteById(folderIdx);
        List<ScrapedArticle> list = sar.findByScrapbookIdx(folderIdx).orElse(null);
        if (list != null) {

        }
        sar.deleteAllByIdIn(list.stream()
                .map(m -> m.getId())
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public void changeFolderName(long userIdx, String folderName, long folderIdx) {
        ScrapFolder sf = sfr.findByIdAndUserIdx(userIdx, folderIdx).orElse(null);
        if (sf == null) {
            throw new ScrapFolderInsertException("폴더가 존재하지 않습니다.");
        }
        sf.setFolderName(folderName);
        sfr.save(sf);
    }

    @Override
    @Transactional
    public void deleteScrapArticle(long userIdx, long folderIdx, long articleIdx) {

        ScrapFolder scrapFolder = sfr.findById(folderIdx)
                .orElseThrow(() -> new NoSuchElementException("스크랩 폴더가 존재하지 않습니다"));

        ScrapedArticle scrapArticle = sar
                .findByScrapbookIdxAndArticleIdx(folderIdx, articleIdx).orElseThrow(() -> new NoSuchElementException("스크랩 아티클이 존재하지 않습니다"));

        sar.delete(scrapArticle);
    }
}
