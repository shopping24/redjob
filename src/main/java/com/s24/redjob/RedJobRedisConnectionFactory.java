package com.s24.redjob;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.lang.annotation.*;

/**
 * Qualifier for the {@link RedisConnectionFactory} to use for RedJob.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface RedJobRedisConnectionFactory {
}
