package com.cc.proxy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * The class CachettlRequest is a model for a cache time to live modification
 * request.
 *
 * @apiNote The JSON string that populates this model looks like {@code {"ttl":
 *          30000}}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CachettlRequest {

	@JsonProperty("ttl")
	private Long ttl;
}
