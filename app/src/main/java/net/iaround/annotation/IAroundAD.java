package net.iaround.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by liangyuanhuan on 10/04/2018.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface IAroundAD {
}
