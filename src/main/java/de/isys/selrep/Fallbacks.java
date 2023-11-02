package de.isys.selrep;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Fallbacks {

    String baseUrl();

    String apiUrl1() default "";

    String apiUrl2() default "";

    String apiUrl3() default "";

}
