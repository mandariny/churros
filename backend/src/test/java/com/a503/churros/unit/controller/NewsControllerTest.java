package com.a503.churros.unit.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.a503.churros.controller.news.NewsController;
import com.a503.churros.dto.news.NewsDocumentationDTO;
import com.a503.churros.dto.news.NewsSearchRequest;
import com.a503.churros.service.news.NewsService;
import com.a503.churros.service.user.UserIdxFromJwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(controllers = NewsController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityAutoConfiguration.class, WebSecurityConfigurerAdapter.class, HttpSecurity.class})})
public class NewsControllerTest {

  @MockBean
  private NewsService newsService;

  @Autowired
  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @MockBean
  private UserIdxFromJwtTokenService userIdxFromJwtTokenService;

  @BeforeEach
  void setUp() {
    this.objectMapper = new ObjectMapper();
  }


  @Test
  @DisplayName("News Search Test")
  public void testNewsSearch() throws Exception {
    //given
    String text = "test";
    int page = 0;
    int size = 10;
    Pageable pageable = PageRequest.of(page, size);

    NewsSearchRequest request = new NewsSearchRequest(text, page, size);

    List<NewsDocumentationDTO> newsContent = new ArrayList<>();
    newsContent.add(
        NewsDocumentationDTO.builder().idx(1L).imgSrc("image").title("text").description("test")
            .link("link").build());
    Slice<NewsDocumentationDTO> news = new SliceImpl<>(newsContent);
    given(newsService.searchByTitleAndDescription(text, pageable)).willReturn(news);

//when
    mockMvc.perform(post("/news/search").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].title").value("text"))
        .andExpect(jsonPath("$.content[0].description").value("test"))
        .andExpect(jsonPath("$.content[0].idx").value(1))
        .andExpect(jsonPath("$.content[0].link").value("link"))
        .andExpect(jsonPath("$.content[0].imgSrc").value("image"))
        .andExpect(jsonPath("$.size").value(1)).andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.first").value(true)).andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.numberOfElements").value(1))
        .andExpect(jsonPath("$.empty").value(false));
//then
    verify(newsService, times(1)).searchByTitleAndDescription(text, pageable);

  }
}