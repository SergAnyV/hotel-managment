package com.asv.feign;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Аннотация для Feign-клиентов.
 * Указывает, что метод требует авторизационного заголовка.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthHeaderFeign {
}
