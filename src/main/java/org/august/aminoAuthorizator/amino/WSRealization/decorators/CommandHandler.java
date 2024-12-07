package org.august.aminoAuthorizator.amino.WSRealization.decorators;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandler {
    String value() default "";
}
