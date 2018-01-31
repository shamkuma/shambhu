package com.trade.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.trade.model.Trade;

@Service
public class TradeServiceImpl implements TradeService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, List<Trade>> tradeDB = new ConcurrentHashMap<>();

	public Map<String, List<Trade>> getTradeDB() {
		return tradeDB;
	}

	public void setTradeDB(Map<String, List<Trade>> tradeDB) {
		this.tradeDB = tradeDB;
	}

	// function to construct object from parameter
	public Trade getTradeObject(String symbol, String source, double price) {
		Trade newTrade = new Trade();
		newTrade.setSymbol(symbol);
		newTrade.setSource(source);
		newTrade.setPrice(price);
		return newTrade;
	}

	// get the price for an existing source of a particular symbol
	@Override
	public double getHighestPrice(String symbol) {
		logger.info("Entering into TradeServiceImpl.getHighestPrice Method");
		List<Trade> existingList = tradeDB.get(symbol);
		if (existingList != null && existingList.size() > 0) {
			existingList.sort((t1, t2) -> {
				if (t1.getPrice() == t2.getPrice()) {
					return 0;
				} else if (t1.getPrice() < t2.getPrice())
					return 1;
				else {
					return -1;
				}
			});
			return existingList.get(0).getPrice();

		} else {
			logger.info("TradeServiceImpl.getHighestPrice Method :No Price Found for symbol:" + symbol);
			return 0.0;
		}

	}

	// get the price for an existing source of a particular symbol
	@Override
	public double getPriceForSource(Trade trade) {
		logger.info("Entering into TradeServiceImpl.getPriceForSource Method");
		List<Trade> existingList = tradeDB.get(trade.getSymbol());
		List<Trade> filteredList = existingList.stream().filter(t -> t.getSource().equals(trade.getSource()))
				.collect(Collectors.toList());
		logger.info("Exiting from TradeServiceImpl.getPriceForSource Method");
		return filteredList.get(0).getPrice();
	}

	// check if source for the particular Symbol already present in the database
	@Override
	public boolean checkSourcePresence(Trade trade) {
		logger.info("Entering into TradeServiceImpl.checkSourcePresence Method");
		if (tradeDB.size() == 0) {
			logger.info("TradeServiceImpl.checkSourcePresence Method: DB is empty");
			return false;
		} else {
			List<Trade> existingList = tradeDB.get(trade.getSymbol());
			if (existingList != null) {
				List<Trade> filteredList = existingList.stream().filter(t -> t.getSource().equals(trade.getSource()))
						.collect(Collectors.toList());
				boolean retVal = filteredList.size() == 0 ? false : true;
				logger.info("TradeServiceImpl.checkSourcePresence Method: Record Present for this Symbol:"
						+ trade.getSymbol() + " Source:" + trade.getSource());
				return retVal;
			} else {
				logger.info("TradeServiceImpl.checkSourcePresence Method: Record Not Present for this Symbol:"
						+ trade.getSymbol() + " Source:" + trade.getSource());
				return false;

			}
		}
	}

	// logic to compare .1% price difference
	@Override
	public boolean checkPriceDiffrencePercentage(double oldPrice, double newPrice) {
		logger.info("Entering into TradeServiceImpl.checkPriceDiffrence Method");
		double percentage = 0;
		if (oldPrice < newPrice) {
			percentage = ((newPrice - oldPrice) * 100) / newPrice;

		} else {
			percentage = ((oldPrice - newPrice) * 100) / oldPrice;
		}
		if (percentage > 0.1) {
			logger.info("TradeServiceImpl.checkPriceDiffrence Method: Price % more than 0.1%");
			return true;
		}
		else {
			logger.info("TradeServiceImpl.checkPriceDiffrence Method: Price % less than 0.1%");
			return false;
		}
			
	}

	// Insert the record in database
	@Override
	public void insertInDB(Trade trade) {
		logger.info("Entering into TradeServiceImpl.InsertInDB Method");

		synchronized (this) {
			List<Trade> list = tradeDB.get(trade.getSymbol());
			if (list == null || list.isEmpty())
				list = new ArrayList<Trade>();
			list.add(trade);
			tradeDB.put(trade.getSymbol(), list);
		}
		logger.info("Exiting from TradeServiceImpl.InsertInDB Method");
	}

	// update price if price difference is more than .1%
	@Override
	public boolean updateInDB(Trade trade) {

		logger.info("Entering into TradeServiceImpl.updateInDB Method");
		synchronized (this) {

			List<Trade> existingList = tradeDB.get(trade.getSymbol());
			List<Trade> tradeSourceList = existingList.stream().filter(t -> t.getSource().equals(trade.getSource()))
					.collect(Collectors.toList());
			if (tradeSourceList != null && tradeSourceList.get(0) != null) {
				tradeSourceList.get(0).setPrice(trade.getPrice());
				logger.info("TradeServiceImpl.updateInDB Method: Update Completed");
				return true;
			} else {
				logger.info("TradeServiceImpl.updateInDB Method: Update Not Completed");
				return false;
			}

		}
		
	}

}
