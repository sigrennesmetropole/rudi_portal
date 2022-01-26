package org.rudi.common.facade.config.filter;

import lombok.Data;

import java.io.Serializable;

/**
 * @author FNI18300
 */
@Data
public class Tokens implements Serializable {

	private static final long serialVersionUID = -3091241502302292087L;

	private String jwtToken;

	private String refreshToken;
}
