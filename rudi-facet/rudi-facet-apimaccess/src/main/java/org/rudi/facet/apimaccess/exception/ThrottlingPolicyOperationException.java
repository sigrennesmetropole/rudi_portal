package org.rudi.facet.apimaccess.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.rudi.facet.apimaccess.bean.PolicyLevel;
import org.rudi.facet.apimaccess.bean.SearchCriteria;

public class ThrottlingPolicyOperationException extends APIManagerException {

	private static final long serialVersionUID = 7944845060234340573L;

	public ThrottlingPolicyOperationException(@Nonnull SearchCriteria searchCriteria, @Nonnull PolicyLevel policyLevel,
			@Nullable String username, @Nonnull Throwable cause) {
		super(String.format("Error with policyLevel=%s for username=%s and searchCriteria=%s", policyLevel, username,
				searchCriteria), cause);
	}

	public ThrottlingPolicyOperationException(@Nonnull String policyName, @Nonnull PolicyLevel policyLevel,
			@Nullable String username, @Nonnull Throwable cause) {
		super(String.format("Error with policyLevel=%s for username=%s and policyName=%s", policyLevel, username,
				policyName), cause);
	}

	public ThrottlingPolicyOperationException(SearchCriteria searchCriteria, PolicyLevel policyLevel) {
		super(String.format("Error with policyLevel=%s and searchCriteria=%s", policyLevel, searchCriteria));
	}
}
