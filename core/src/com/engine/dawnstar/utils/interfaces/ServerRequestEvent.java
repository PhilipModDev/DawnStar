package com.engine.dawnstar.utils.interfaces;

public interface ServerRequestEvent<T> {
     void command(T object);
}
