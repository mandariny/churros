package com.a503.churros.repository.news;

import com.a503.churros.entity.news.Read;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadRepository extends JpaRepository<Read, Long> {
    Optional<Read> findByUserIdxAndArticleIdx(long userIdx , long articleIdx);
}
