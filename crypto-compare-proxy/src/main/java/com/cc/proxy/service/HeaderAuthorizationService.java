package com.cc.proxy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

/**
 * The class HeaderAuthorizationService is responsible for authorizing the
 * {@code x-admin-key} header. A controller method annotated with
 * {@code @PreAuthorize("@headerAuthorizationService.authorize(#request)")} with
 * security configuration allowing for pre-method authorization, forces the
 * filter chain to authorize using this class.
 *
 * Failure to send the expected header with the expected value when invoking the
 * said annotated controller method, will result in an
 * {@code AccessDeniedException}. Clients will receive a detailed JSON error
 * message in a format such as:
 *
 * <pre>
 * {
 *   "timestamp": "2022-08-28",
 *   "status": 403,
 *   "error": "Forbidden",
 *   "message": "Access Denied",
 *   "path": "/cachettl"
 * }
 * </pre>
 */
@Service
public class HeaderAuthorizationService {

	// @formatter:off
	@Value("${admin.key}") private String headerValue;
	@Value("${admin.key.header}") private String headerName;
	// @formatter:on

	public boolean authorize(WebRequest request) {
		return null != request.getHeader(headerName) && request.getHeader(headerName).equals(headerValue);
	}

}
