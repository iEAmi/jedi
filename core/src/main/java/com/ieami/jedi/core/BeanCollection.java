package com.ieami.jedi.core;

public interface BeanCollection {

    <T, R extends T> BeanCollection addSingleton(Class<T> inf, Class<R> impl);
}
