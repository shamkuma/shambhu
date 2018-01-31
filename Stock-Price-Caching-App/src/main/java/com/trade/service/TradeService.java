package com.trade.service;

import com.trade.model.Trade;

public interface TradeService {
	public boolean updateInDB( Trade trade);
	public void insertInDB(Trade trade);
	public double getHighestPrice( String symbol);
	public boolean checkSourcePresence(Trade trade);
	public boolean checkPriceDiffrencePercentage(double oldPrice, double newPrice);
	public double getPriceForSource( Trade trade);
}
