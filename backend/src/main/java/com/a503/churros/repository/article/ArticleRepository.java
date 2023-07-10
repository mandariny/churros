package com.a503.churros.repository.article;

import com.a503.churros.entity.article.Article;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article , Long> {
    Optional<Article> findFirstByIdx(long idx);
}
