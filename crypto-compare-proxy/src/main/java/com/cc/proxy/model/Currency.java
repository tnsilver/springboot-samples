package com.cc.proxy.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Wrapper container for JSON results from api endpoint
 * {@linkplain https://min-api.cryptocompare.com/data/pricemulti}
 */
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Currency {

	// @formatter:off
	@JsonProperty("Response") private String response;
	@JsonProperty("Message") private String message;
	@JsonProperty("HasWarning") Boolean hasWarning;
	@JsonProperty("Type") Integer type;
	@JsonProperty("RateLimit") Object rateLimit;
	// @formatter:on

	/**
	 * The key in this map is the crypto currency symbol, and the values are pairs
	 * of currency codes and their rates.
	 */
	Map<String, Rate> details = new LinkedHashMap<>();

	@JsonAnySetter
	void setDetail(String key, Rate value) {
		details.put(key, value);
	}

}
