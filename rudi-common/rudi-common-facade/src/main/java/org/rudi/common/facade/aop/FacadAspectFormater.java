/**
 * RUDI Portail
 */
package org.rudi.common.facade.aop;

/**
 * @author FNI18300
 *
 */
public interface FacadAspectFormater {

	boolean accept(Object item);

	String format(Object item);

}
