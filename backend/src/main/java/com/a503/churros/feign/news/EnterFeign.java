package com.a503.churros.feign.news;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "entertain", url = "https://entertain.naver.com//now/read")
public interface EnterFeign {

    @RequestMapping(method = RequestMethod.GET)
    String getArticle(@RequestParam("oid") String oid, @RequestParam("aid") String aid);
}