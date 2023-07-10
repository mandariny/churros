package com.a503.churros.controller.news;

import com.a503.churros.dto.article.ArticleDTO;
import com.a503.churros.dto.news.ArticleInputDTO;
import com.a503.churros.dto.news.NewsDocumentationDTO;
import com.a503.churros.dto.news.NewsSearchRequest;
import com.a503.churros.service.news.NewsService;
import com.a503.churros.service.user.UserIdxFromJwtTokenService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/news")
@Api("NEWS API")
@RequiredArgsConstructor
public class NewsController {

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    private final NewsService ns;
    private final UserIdxFromJwtTokenService ts;

    @GetMapping("")
    public ResponseEntity<?> getNews(
            @RequestHeader("Authorization")
            String token
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        List<Integer> list = ns.sendRecommend(userId);
        resultMap.put("articles", list);
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<?> getNewsArti(
            @RequestHeader("Authorization")
            String token,
            @PathVariable(value = "articleId") Integer articleId
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        ArticleDTO dto = ns.getArticleInfo(userId, articleId);
        resultMap.put("result", SUCCESS);
        resultMap.put("article", dto);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @GetMapping("/call")
    public ResponseEntity<?> getNewsHtml(
            @RequestParam String url,
            @RequestParam boolean ent
    ) throws UnsupportedEncodingException {

        System.out.println("encoded url = " + url);
        String decodedUrl = URLDecoder.decode(url, "UTF-8");
        System.out.println("decoded url = " + decodedUrl);

        Map<String, Object> resultMap = new HashMap<String, Object>();
        String html = ns.callNaver(decodedUrl, ent);
        resultMap.put("result", SUCCESS);
        resultMap.put("html", html);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }


    @GetMapping("/sample")
    public ResponseEntity<?> getNewsSample() {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Integer> list = ns.sendSample();
        resultMap.put("result", SUCCESS);
        resultMap.put("articles", list);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @PutMapping("/read")
    public ResponseEntity<?> putRead(
            @RequestHeader("Authorization")
            String token,
            @RequestBody ArticleInputDTO dto
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        ns.saveReadArti(userId, dto.getArticleId());
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);

    }

    @PutMapping("/like")
    public ResponseEntity<?> postLike(
            @RequestHeader("Authorization")
            String token,
            @RequestBody ArticleInputDTO dto
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        ns.recordLike(userId, dto.getArticleId());
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @GetMapping("/like")
    public ResponseEntity<?> getLike(
            @RequestHeader("Authorization")
            String token
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        List<Long> list = ns.getLikeList(userId);
        resultMap.put("articles", list);
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @PostMapping("/dislike")
    public ResponseEntity<?> postDisLike(
            @RequestHeader("Authorization")
            String token,
            @RequestBody ArticleInputDTO dto
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        ns.recordDisLike(userId, dto.getArticleId());
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    /**
     * 현재 저장된 news의 검색결과를 반환합니다
     *
     * @param newsSearchRequest 뉴스 서치에 필요한 요청값
     * @return 검색된 결과와 상태코드
     * @author Lee an chae
     */
    @PostMapping("/search")
    public ResponseEntity<?> newsSearch(@RequestBody NewsSearchRequest newsSearchRequest) {
        String text = newsSearchRequest.getText();
        int page = newsSearchRequest.getPage();
        int size = newsSearchRequest.getSize();
        Pageable pageable = PageRequest.of(page, size);
        Slice<NewsDocumentationDTO> news = ns.searchByTitleAndDescription(text, pageable);

        return ResponseEntity.ok(news);
    }


}
