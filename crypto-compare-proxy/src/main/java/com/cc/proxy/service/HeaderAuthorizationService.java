package com.cc.proxy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

/**
 * The class HeaderAuthorizationService is responsible for authorizing the {@code x-admin-key} header.
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
