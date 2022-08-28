
package com.cc.proxy.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

/**
 * wrapper container for JSON results from api endpoint
 * {@linkplain https://min-api.cryptocompare.com/data/all/coinlist}
 */
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Response", "Message", "Data" })
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CoinList {

	// @formatter:off
	@JsonProperty("Response") private String response;
	@JsonProperty("Message") private String message;
	@JsonProperty("Data") private Map<String, CryptoCoin> data = new LinkedHashMap<>();
	// @formatter:on

}
