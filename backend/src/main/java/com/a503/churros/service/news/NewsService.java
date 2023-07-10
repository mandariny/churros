package com.a503.churros.service.news;

import com.a503.churros.dto.article.ArticleDTO;
import com.a503.churros.dto.news.NewsDocumentationDTO;
import com.a503.churros.entity.news.Like;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NewsService {
    List<Integer> sendRecommend(long userId);
    List<Integer> sendSample();
    void saveReadArti(long userId, long articleId);
    void recordLike(long userId, long articleId);
    List<Long> getLikeList(long userIdx);
    void recordDisLike(long userId, long articleId);

    ArticleDTO getArticleInfo(long userId , long articleId);
    String callNaver(String url , boolean t);

    /**
     * 문장을 통해 검색 요청했을 경우 요청 페이지에 대한 검색 결과를 리턴함
     *
     * @author Lee an chae
     * @param query 검색시 필요한 문장
     * @param pageable 검색시 반환하는 사이즈 size와 어떤 페이지를 반환해야되는지에 대한 정보를 담고있음 page
     * @return 검색 결과와 요청 받은 page 값들 리턴
     */
    Slice<NewsDocumentationDTO> searchByTitleAndDescription(String query, Pageable pageable);
}
