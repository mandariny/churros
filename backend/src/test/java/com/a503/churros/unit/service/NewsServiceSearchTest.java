package com.a503.churros.unit.service;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.a503.churros.dto.news.NewsDocumentationDTO;
import com.a503.churros.entity.news.NewsDocumentation;
import com.a503.churros.feign.news.NaverFeign;
import com.a503.churros.feign.news.NewsFeign;
import com.a503.churros.repository.article.ArticleRepository;
import com.a503.churros.repository.news.DisLikeRepository;
import com.a503.churros.repository.news.LikeRepository;
import com.a503.churros.repository.news.ReadRepository;
import com.a503.churros.service.news.NewsService;
import com.a503.churros.service.news.NewsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
class NewsServiceSearchTest {

  private static final DockerImageName ELASTICSEARCH_IMAGE = DockerImageName.parse(
      "docker.elastic.co/elasticsearch/elasticsearch:7.17.9");
  private NewsService newsService;
  private ElasticsearchOperations elasticsearchOperations;
  private List<NewsDocumentation> newsDocumentationList;
  private PageRequest pageable;
  private ElasticsearchContainer container;

  @Mock
  private ReadRepository readRepository;
  @Mock
  private LikeRepository likeRepository;
  @Mock
  private DisLikeRepository disLikeRepository;
  @Mock
  private ArticleRepository articleRepository;
  @Mock
  private NewsFeign newsFeign;

  @Mock
  private NaverFeign naverFegin;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    //ElasticSearch 이미지 pull 및 기본 설정
    container = new ElasticsearchContainer(
        ELASTICSEARCH_IMAGE).withEnv("ELASTIC_PASSWORD", "elastic_password")
        .withEnv("xpack.security.enabled", "true");
    //ElasticSearch container start
    container.start();
    //Elasticsearch 연결
    ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        .connectedTo(container.getHttpHostAddress()).withBasicAuth("elastic", "elastic_password")
        .build();
    //필요한 객체 생성
    elasticsearchOperations = new ElasticsearchRestTemplate(
        RestClients.create(clientConfiguration).rest());
    newsService = new NewsServiceImpl(readRepository, likeRepository, disLikeRepository,
        articleRepository,elasticsearchOperations, newsFeign, naverFegin );
    //test Entity 생성
    NewsDocumentation newsDoc = new NewsDocumentation("1", "Test news title",
        "Test news description", 1L, "https://www.example.com",
        "https://www.example.com/image.jpg");

    // Create the "news" index
    elasticsearchOperations.indexOps(NewsDocumentation.class).create();

    // Create test data
    elasticsearchOperations.save(newsDoc);
    //강제로 인덱싱
    elasticsearchOperations.indexOps(NewsDocumentation.class).refresh();


  }

  @AfterEach
  void tearDown() {
    container.stop();
  }

  @Test
  void testSearchByTitleAndDescription() {
    // Given
    String query = "test";
    PageRequest pageable = PageRequest.of(0, 10);

    // When
    Slice<NewsDocumentationDTO> results = newsService.searchByTitleAndDescription(query, pageable);
    // Then

    NewsDocumentationDTO result = results.getContent().get(0);
    assertEquals("Test news title", result.getTitle());
    assertEquals("Test news description", result.getDescription());
    assertEquals(1L, result.getIdx());
    assertEquals("https://www.example.com", result.getLink());
    assertEquals("https://www.example.com/image.jpg", result.getImgSrc());
  }


}
