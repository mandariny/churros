package com.a503.churros.repository.news;

import com.a503.churros.entity.news.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like , Long> {
    List<Like> findAllByUserIdx(long userIdx);
    Optional<Like> findByUserIdxAndArticleIdx(long userIdx , long articleIdx);
}