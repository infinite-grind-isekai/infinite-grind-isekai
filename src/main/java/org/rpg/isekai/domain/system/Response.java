package org.rpg.isekai.domain.system;

import lombok.Getter;

@Getter
public class Response<T extends Message> {
    private final ResponseCode code;
    private final String message;

    public Response(ResponseCode code, T rawMessage) {
        this.code = code;
        this.message = rawMessage.getMessage(rawMessage);
    }

    public static <T extends Message> Response<T> success(T rawMessage) {
        return new Response<>(ResponseCode.OK, rawMessage);
    }
}