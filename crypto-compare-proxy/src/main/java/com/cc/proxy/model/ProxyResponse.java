package com.cc.proxy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({ "Response", "Message", "Data" })
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProxyResponse<T> implements DetailedResponse {

	// @formatter:off
	@JsonProperty("response") private String response;
	@JsonProperty("message") private String message;
	@JsonProperty("data") private T data;
	// @formatter:on

}
