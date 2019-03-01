package net.iaround.im.task;

/**
 * Created by liangyuanhuan on 07/12/2017.
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define task property
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface TaskProperty {
    public static final String OPTIONS_HOST = "stn_host";
    public static final String OPTIONS_CGI_PATH = "cgi_path";
    public static final String OPTIONS_CHANNEL_SHORT_SUPPORT = "short_support";
    public static final String OPTIONS_CHANNEL_LONG_SUPPORT = "long_support";
    public static final String OPTIONS_CMD_ID = "command_id";

    String host() default "";

    String path();

    boolean shortChannelSupport() default false;

    boolean longChannelSupport() default true;

    int cmdID() default -1;
}
