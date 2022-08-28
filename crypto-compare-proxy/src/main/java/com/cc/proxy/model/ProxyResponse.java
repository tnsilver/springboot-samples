package com.cc.proxy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "response", "message", "data" })
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProxyResponse<T> {

	// @formatter:off
	@JsonProperty("response") private String response;
	@JsonProperty("message") private String message;
	@JsonProperty("data") private T data;
	// @formatter:on

}
