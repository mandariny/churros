package com.a503.churros.repository.scrap;

import com.a503.churros.entity.scrap.ScrapFolder;
import com.a503.churros.entity.scrap.ScrapedArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapedArticleRepository extends JpaRepository<ScrapedArticle, Long> {
    Optional<List<ScrapedArticle>> findByScrapbookIdx(long scrapbookIdx);
    Optional<ScrapedArticle> findByScrapbookIdxAndArticleIdx(long scrapbookIdx , long articleIdx);
    void deleteAllByIdIn(List<Long> ids);
}
