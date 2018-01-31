package com.trade.model;


import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name="Trade")
public class Trade implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8146588530665695946L;
	private String symbol;
	private String source;
	private Double price;

	public Trade() {
		
	}
	public Trade(String symbol,String source, Double price) {
		this.symbol=symbol;
		this.source=source;
		this.price=price;
	}
	public String getSymbol() {
		return symbol;
	}

	@XmlElement
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	@XmlElement
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	@XmlElement
	public void setPrice(Double price) {
		this.price = price;
	}
	@Override
	public String toString(){
		return "Symbol:"+symbol+" Source:"+source+" Price:"+price;
	}
	
}


