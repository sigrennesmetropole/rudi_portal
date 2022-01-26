package org.rudi.facet.apimaccess.helper.cache;

import freemarker.template.Template;
import org.ehcache.spi.serialization.Serializer;

import java.nio.ByteBuffer;

/**
 * Pour une raison inconnue, Ehcache nécessite un {@link Serializer} pour {@link Template} mais Spring ne semble pas l'utiliser.
 * Le cache fonctionne pourtant bien. C'est pourquoi on n'implémente les méthodes qu'avec des "return null" pour faire au plus simple.
 * <p>
 * Tentatives :
 * <ul>
 *     <li>ajouter la propriété : spring.cache.jcache.config=classpath:cache/ehcache_config.xml, tel que décrit dans :</li>
 *     <ul>
 *         <li>https://www.baeldung.com/spring-boot-ehcache</li>
 *         <li>https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-jcache</li>
 *     </ul>
 *     <li>annoter avec @EnableCaching la classe CacheConfig</li>
 * <p>
 * Source : https://www.ehcache.org/blog/2016/05/12/ehcache3-serializers.html
 */
public class TemplateSerializer implements Serializer<Template> {

	public TemplateSerializer(@SuppressWarnings("unused") /* Paramètre nécessaire à Ehcache */ ClassLoader loader) {
	}

	@Override
	public ByteBuffer serialize(Template template) {
		return null; // cf Javadoc de la classe
	}

	@Override
	public Template read(ByteBuffer byteBuffer) throws ClassNotFoundException {
		return null; // cf Javadoc de la classe
	}

	@Override
	public boolean equals(Template template, ByteBuffer byteBuffer) throws ClassNotFoundException {
		return template.equals(read(byteBuffer));
	}
}
