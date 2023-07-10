package com.a503.churros.controller.scrap;


import com.a503.churros.dto.scrap.ScrapArticleRequestDTO;
import com.a503.churros.dto.scrap.ScrapFolderDTO;
import com.a503.churros.dto.scrap.ScrapInputDTO;
import com.a503.churros.service.scrap.ScrapService;
import com.a503.churros.service.user.UserIdxFromJwtTokenService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/scrap")
@Api("SCRAP API")
@RequiredArgsConstructor
public class ScrapController {

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    private final ScrapService ss;
    private final UserIdxFromJwtTokenService ts;

    @GetMapping("")
    public ResponseEntity<?> getScrap(
            @RequestHeader("Authorization")
            String token
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        List<ScrapFolderDTO> folderList = ss.getFolderList(userId);
        if (folderList == null) {
            resultMap.put("empty", true);
        } else {
            resultMap.put("empty", false);
            resultMap.put("folder", folderList);
        }
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @GetMapping("/folders")
    public ResponseEntity<?> getScrap(
            @RequestHeader("Authorization")
            String token,
            @RequestParam Integer articleIdx

    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        List<ScrapFolderDTO> folderList = ss.getFolders(userId, articleIdx);
        resultMap.put("folder", folderList);
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @GetMapping("/{scrapbookId}")
    public ResponseEntity<?> getScrap(
            @RequestHeader("Authorization")
            String token,
            @PathVariable(value = "scrapbookId") long scrapbookId
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        List<Long> articleList = ss.getArticleList(scrapbookId, userId);
        if (articleList == null) {
            resultMap.put("empty", true);
        } else {
            resultMap.put("empty", false);
            resultMap.put("articles", articleList);
        }
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @PostMapping("/book")
    public ResponseEntity<?> postScrapBook(
            @RequestHeader("Authorization")
            String token,
            @RequestBody ScrapInputDTO dto
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        long folderIdx = ss.insertFolderName(userId, dto.getFolderName());
        resultMap.put("folderIdx", folderIdx);
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @PutMapping("/book")
    public ResponseEntity<?> putScrapBooK(
            @RequestHeader("Authorization")
            String token,
            @RequestBody ScrapInputDTO dto
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        ss.changeFolderName(userId, dto.getFolderName(), dto.getFolderIdx());
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @DeleteMapping("/book")
    public ResponseEntity<?> deleteScrapBook(
            @RequestHeader("Authorization")
            String token,
            @RequestBody ScrapInputDTO dto
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        long userId = ts.extractIdxFromToken(token);
        ss.deleteFolder(userId, dto.getFolderIdx());
        resultMap.put("result", SUCCESS);
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @PutMapping("/article")
    public ResponseEntity<?> putScrapArticle(
            @RequestHeader("Authorization")
            String token,
            @RequestBody ScrapArticleRequestDTO dto
    ) {
        Map<String, Object> resultMap = new HashMap<>();
        long userId = ts.extractIdxFromToken(token);
        if(dto.getScrapped()){
            ss.saveArticle(userId, dto.getFolderIdx(), dto.getArticleIdx());
            resultMap.put("message", String.format("article %d added into folder %d (%s)", dto.getArticleIdx(), dto.getFolderIdx(), dto.getFolderName()));
        }
        else{
            ss.deleteScrapArticle(userId, dto.getFolderIdx(), dto.getArticleIdx());
            resultMap.put("message", String.format("article %d removed from folder %d (%s)", dto.getArticleIdx(), dto.getFolderIdx(), dto.getFolderName()));
        }

        resultMap.put("result", SUCCESS);

        return ResponseEntity.ok(resultMap);
    }
}
