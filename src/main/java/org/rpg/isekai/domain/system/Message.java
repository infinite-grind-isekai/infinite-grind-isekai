package org.rpg.isekai.domain.system;

public interface Message<T> {
    String getMessage(T rawMessage);
}
