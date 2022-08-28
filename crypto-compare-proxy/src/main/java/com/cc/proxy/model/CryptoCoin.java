package com.cc.proxy.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * container for JSON results from api endpoint
 * {@linkplain https://min-api.cryptocompare.com/data/all/coinlist}
 */
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Response", "Message", "id", "symbol", "coinName", "algorithm", "toUSD" })
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class CryptoCoin {

    // @formatter:off
	@JsonProperty("Response") private String response;
	@JsonProperty("Message") private String message;
	// @formatter:on
	private Long id;
	private String symbol;
	private String coinName;
	private String algorithm;
	private BigDecimal toUSD;

	// getters are used during serialization (reading an object and creating json)

	// setters are used during deserialization (reading json and creating an object)

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@JsonGetter("id")
	public Long getId() {
		return id;
	}

	@JsonSetter("Id")
	public void setId(Long id) {
		this.id = id;
	}

	@JsonGetter("symbol")
	public String getSymbol() {
		return symbol;
	}

	@JsonSetter("Symbol")
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@JsonGetter("coinName")
	public String getCoinName() {
		return coinName;
	}

	@JsonSetter("CoinName")
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	@JsonGetter("algorithm")
	public String getAlgorithm() {
		return algorithm;
	}

	@JsonSetter("Algorithm")
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@JsonGetter("toUSD")
	public BigDecimal getToUSD() {
		return toUSD;
	}

	@JsonSetter("USD")
	public void setToUSD(BigDecimal toUSD) {
		this.toUSD = toUSD;
	}

}
