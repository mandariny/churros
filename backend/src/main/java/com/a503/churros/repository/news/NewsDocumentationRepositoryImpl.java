package com.a503.churros.repository.news;


import com.a503.churros.entity.news.NewsDocumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class NewsDocumentationRepositoryImpl implements NewsDocumentationCustomRepository {

  private final ElasticsearchOperations elasticsearchOperations;


  @Override
  public List<NewsDocumentation> findByTitleAndDescription(String query, Pageable pageable) {
    QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(query, "title", "description");
//        QueryBuilder queryBuilder = QueryBuilders.matchQuery("title", query);
    NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
        .withQuery(queryBuilder)
        .withPageable(pageable)
        .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
        .build();

    SearchHits<NewsDocumentation> searchHits = elasticsearchOperations.search(searchQuery,
        NewsDocumentation.class);
    List<NewsDocumentation> newsDocumentation = new ArrayList<>();
    for (SearchHit<NewsDocumentation> hit : searchHits) {
      newsDocumentation.add(hit.getContent());
    }

    return newsDocumentation;
  }
}