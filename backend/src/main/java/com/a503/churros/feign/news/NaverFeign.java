package com.a503.churros.feign.news;

import feign.Param;
import feign.RequestInterceptor;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "naver", url = "https://n.news.naver.com")
public interface NaverFeign {

    @RequestMapping(method = RequestMethod.GET, value = "/mnews/article/{articleId}")
    String getArticle(@PathVariable("articleId") String articleId);
}
