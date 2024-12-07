package org.august.aminoAuthorizator.amino.WSRealization.decorators;

import org.august.AminoApi.dto.enums.UserRole;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Properties {
    UserRole value() default UserRole.MEMBER;
}
