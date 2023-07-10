package com.a503.churros.service.news;

import com.a503.churros.dto.article.ArticleDTO;
import com.a503.churros.dto.news.NewsDocumentationDTO;
import com.a503.churros.entity.article.Article;
import com.a503.churros.entity.news.DisLike;
import com.a503.churros.entity.news.Like;
import com.a503.churros.entity.news.NewsDocumentation;
import com.a503.churros.entity.news.Read;
import com.a503.churros.feign.news.EnterFeign;
import com.a503.churros.feign.news.NaverFeign;
import com.a503.churros.feign.news.NewsFeign;
import com.a503.churros.repository.article.ArticleRepository;
import com.a503.churros.repository.news.DisLikeRepository;
import com.a503.churros.repository.news.LikeRepository;
import com.a503.churros.repository.news.NewsDocumentationRepository;
import com.a503.churros.repository.news.ReadRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class NewsServiceImpl implements NewsService {
  private final NewsDocumentationRepository newsDocumentationRepository;
  private final ReadRepository rr;
  private final LikeRepository lr;
  private final DisLikeRepository dr;
  private final ArticleRepository ar;

  private final ElasticsearchOperations elasticsearchOperations;
  private final NewsFeign fc;
  private final NaverFeign nf;
  private final EnterFeign ef;


    public List<Integer> sendRecommend(long userId) {
        List<Integer> list = fc.getRecomList(userId).getRecommendList();
        return list;
    }

    public List<Integer> sendSample() {
        List<Integer> list = fc.getSampleList().getRecommendList();
        System.out.println(list);
        return list;
    }

    @Override
    public void saveReadArti(long userId, long articleId) {
        Read read = rr.findByUserIdxAndArticleIdx(userId, articleId).orElse(null);
        if (read == null) {
            read = Read.builder()
                    .userIdx(userId)
                    .articleIdx(articleId)
                    .build();
        }
        read.setReadDate(LocalDateTime.now());
        read.setValidDate(LocalDateTime.now().plusDays(30L));

        rr.save(read);
    }

    @Override
    @Transactional
    public void recordLike(long userId, long articleId) {
        Like like = lr.findByUserIdxAndArticleIdx(userId, articleId).orElse(null);
        if (like == null) {
            like = Like.builder()
                    .userIdx(userId)
                    .articleIdx(articleId)
                    .build();
            lr.save(like);
        } else {
            lr.delete(like);
        }
    }

    public List<Long> getLikeList(long userIdx) {


        List<Long> result =  lr.findAllByUserIdx(userIdx)
                .stream()
                .map(item -> item.getArticleIdx())
                .collect(Collectors.toList());
        Collections.reverse(result);
        return result;
    }

    @Override
    public void recordDisLike(long userId, long articleId) {
        DisLike dis = dr.findByUserIdxAndArticleIdx(userId, articleId).orElse(null);
        if (dis == null) {
            dis = DisLike.builder()
                    .userIdx(userId)
                    .articleIdx(articleId)
                    .build();
            dr.save(dis);
        }
    }

    @Override
    public ArticleDTO getArticleInfo(long userId, long articleId) {
        Article article = ar.findFirstByIdx(articleId).orElse(null);
        if (article == null) {
            return null;
        }
        ArticleDTO dto = new ArticleDTO().of(article);
        Like like = lr.findByUserIdxAndArticleIdx(userId, articleId).orElse(null);
        if (like != null) {
            dto.setLike(true);
        }
        return dto;
    }


    @Override
    public String callNaver(String url , boolean t) throws PatternSyntaxException {
        System.out.println("callNaver: url = " + url);
        if(t){
            String extractingOidValueRegex = "(?<=oid=)([^&]+)";
            Pattern extractingOidValuePattern = Pattern.compile(extractingOidValueRegex);
            Matcher extractedOid = extractingOidValuePattern.matcher(url);

            String extractingAidValueRegex = "(?<=aid=)(.*)";
            Pattern extractingAidValuePattern = Pattern.compile(extractingAidValueRegex);
            Matcher extractedAid = extractingAidValuePattern.matcher(url);

            System.out.println("oid value found = " + extractedOid.find());
            String oidValue = extractedOid.group();
            System.out.println("oid = " + oidValue);

            System.out.println("aid value found = " + extractedAid.find());
            String aidValue = extractedAid.group();
            System.out.println("aid = " + aidValue);

            return ef.getArticle(oidValue, aidValue);
        }else{
            return nf.getArticle(url);
        }
    }

  /**
   * 문장을 통해 검색 요청했을 경우 요청 페이지에 대한 검색 결과를 리턴함
   *
   * @param query    검색시 필요한 문장
   * @param pageable 검색시 반환하는 사이즈 size와 어떤 페이지를 반환해야되는지에 대한 정보를 담고있음 page
   * @return 검색 결과와 요청 받은 page 값들 리턴
   * @author Lee an chae
   */
  public Slice<NewsDocumentationDTO> searchByTitleAndDescription(String query, Pageable pageable) {
    List<NewsDocumentation> newsDocumentationList = newsDocumentationRepository.findByTitleAndDescription(query, pageable);
    List<NewsDocumentationDTO> newsDocumentationDTOList = new ArrayList<>();
    for (NewsDocumentation newsDocumentation : newsDocumentationList) {
      newsDocumentationDTOList.add(convertToDto(newsDocumentation));
    }

    if (newsDocumentationDTOList.isEmpty()) {
      return new SliceImpl<>(newsDocumentationDTOList, pageable, false);
    }

    boolean hasNext = newsDocumentationDTOList.size() == pageable.getPageSize();
    return new SliceImpl<>(newsDocumentationDTOList, pageable, hasNext);
  }

  /**
   * NewsDocumentation Entity를 NewsDocumentationDTO로 변환
   *
   * @param newsDocumentation NewsDocumentation Entity
   * @return 변환된 NewsDocumentationDTO
   * @author Lee an chae
   */

  private NewsDocumentationDTO convertToDto(NewsDocumentation newsDocumentation) {
    return NewsDocumentationDTO.builder().
        title(newsDocumentation.getTitle()).
        description(newsDocumentation.getDescription()).
        idx(newsDocumentation.getIdx()).
        link(newsDocumentation.getLink()).
        imgSrc(newsDocumentation.getImgSrc()).
        build();
  }





}
