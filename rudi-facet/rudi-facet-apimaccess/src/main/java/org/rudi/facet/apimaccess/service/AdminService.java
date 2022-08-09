package org.rudi.facet.apimaccess.service;

import org.rudi.facet.apimaccess.exception.AdminOperationException;

public interface AdminService {
	void assignRoleToUser(String role, String username) throws AdminOperationException;
}
