package com.a503.churros.dto.news;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewsListDTO {

    List<Integer> recommendList ;
}
