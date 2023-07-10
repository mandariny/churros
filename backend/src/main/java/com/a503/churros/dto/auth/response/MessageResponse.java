package com.a503.churros.dto.auth.response;


import lombok.Builder;
import lombok.Data;

@Data
public class MessageResponse {
    private String result;


    private String msg;

    @Builder
    public MessageResponse(String result, String msg){
        this.result = result;
        this.msg = msg;
    }
}
