package org.rpg.isekai.domain.system;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    OK(200), ERROR(500), END(999);

    private int code;
    private String message;

    ResponseCode(int code) {
        this.code = code;
    }
}
