package org.rudi.wso2.mediation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indique que la classe annotée correspond à des propriétés chargées depuis le fichier de properties des Handler WSO2 :
 * <code>org.rudi.wso2.handler.properties</code>.
 *
 * <p>
 * Comportement inspiré de l'annotation Spring <code>org.springframework.boot.context.properties.ConfigurationProperties</code>
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WSO2HandlerProperties {
	/**
	 * Cf doc de <code>org.springframework.boot.context.properties.ConfigurationProperties</code>
	 */
	String prefix();
}
