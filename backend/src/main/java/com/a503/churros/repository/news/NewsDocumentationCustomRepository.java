package com.a503.churros.repository.news;

import com.a503.churros.entity.news.NewsDocumentation;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface NewsDocumentationCustomRepository {
  List<NewsDocumentation> findByTitleAndDescription(String query, Pageable pageable);
}
