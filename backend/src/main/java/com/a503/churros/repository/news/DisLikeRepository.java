package com.a503.churros.repository.news;

import com.a503.churros.entity.news.DisLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisLikeRepository extends JpaRepository<DisLike , Long> {
    Optional<DisLike> findByUserIdxAndArticleIdx(long userId , long articleIdx);
}
