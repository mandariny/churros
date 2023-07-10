package com.a503.churros.repository.news;

import com.a503.churros.dto.news.NewsDocumentationDTO;
import com.a503.churros.entity.news.NewsDocumentation;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NewsDocumentationRepository extends
    ElasticsearchRepository<NewsDocumentation, Long>, NewsDocumentationCustomRepository {

}
