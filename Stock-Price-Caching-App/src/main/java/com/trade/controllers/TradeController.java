package com.trade.controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.trade.exception.PriceNotFoundException;
import com.trade.model.Response;
import com.trade.model.Trade;
import com.trade.service.TradeService;

@RestController
public class TradeController {

	private static Properties prop;
	private TradeService tradeService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/consumePrice", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_XML)
	public @ResponseBody Response consumePrice(@RequestBody Trade trade) {
		logger.info("Entering into TradeController.consumePrice-Post Version Method");

		Response response = new Response();
		if (!tradeService.checkSourcePresence(trade)) {
			tradeService.insertInDB(trade);
			response.setStatus(true);
			response.setMessage(prop.getProperty("success_msg1"));
			logger.info("Doing Insert TradeController.consumePrice-Post");
			return response;
		} else {
			// check this source present for this symbol
			if (tradeService.checkSourcePresence( trade)) {
				double oldPrice = tradeService.getPriceForSource(trade);

				if (tradeService.checkPriceDiffrencePercentage(oldPrice, trade.getPrice())) {
					tradeService.updateInDB(trade);
					response.setStatus(true);
					response.setMessage(prop.getProperty("success_msg2"));
					logger.info("Doing update TradeController.consumePrice-Post Price Diffrence more than .1%");
					return response;
				} else {
					response.setStatus(false);
					response.setMessage(prop.getProperty("error_msg1"));
					logger.info("No update TradeController.consumePrice-Post Price Diffrence less than .1%");
					return response;
				}
			} else {
				tradeService.insertInDB(trade);
				response.setStatus(true);
				response.setMessage(prop.getProperty("success_msg2"));
				logger.info("Doing insert on TradeController.consumePrice-Post when new symbol entry arrived");
				return response;
			}

		}

	}

	@RequestMapping(value = "/consumePrice", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_XML)
	public @ResponseBody Response consumePrice(@RequestParam("symbol") String symbol,
			@RequestParam("source") String source, @RequestParam("price") double price) {
		Trade trade=new Trade(symbol,source,price);
		logger.info("Entering into TradeController.consumePrice-Get Version Method");
		Response response = new Response();
		if (!tradeService.checkSourcePresence(trade)) {
			tradeService.insertInDB(trade);
			response.setStatus(true);
			response.setMessage(prop.getProperty("success_msg1"));
			logger.info("Doing Insert TradeController.consumePrice-Get");
			return response;
		} else {
			// check this source present for this symbol
			if (tradeService.checkSourcePresence( trade)) {
				double oldPrice = tradeService.getPriceForSource(trade);

				if (tradeService.checkPriceDiffrencePercentage(oldPrice, trade.getPrice())) {
					tradeService.updateInDB(trade);
					response.setStatus(true);
					response.setMessage(prop.getProperty("success_msg2"));
					logger.info("Doing update TradeController.consumePrice-Get Price Diffrence more than .1%");
					return response;
				} else {
					response.setStatus(false);
					response.setMessage(prop.getProperty("error_msg1"));
					logger.info("No update TradeController.consumePrice-Get Price Diffrence less than .1%");
					return response;
				}
			} else {
				tradeService.insertInDB(trade);
				response.setStatus(true);
				response.setMessage(prop.getProperty("success_msg2"));
				logger.info("Doing insert on TradeController.consumePrice-Get when new symbol entry arrived");
				return response;
			}

		}

	}

	@RequestMapping(value = "/getPrice/{symbol}", method = RequestMethod.GET)
	@Produces(MediaType.TEXT_PLAIN)
	public String getPrice(@PathVariable String symbol) {
		String msg = null;
		logger.info("Entering into TradeController.getPrice Version Method");
		if (tradeService.getHighestPrice(symbol)<=0) {
			msg = prop.getProperty("error_msg2") + symbol;
			logger.error("TradeController.getPrice -Price Not Found for Symbol:"+symbol);
			throw new PriceNotFoundException(msg);
			

			// return msg;
		}else {

		
		msg = prop.getProperty("message") + symbol + " is: " + tradeService.getHighestPrice(symbol);
		logger.info("TradeController.getPrice -Price  for Symbol:"+symbol+ " is:"+tradeService.getHighestPrice(symbol));
		}
		return msg;
	}

	public TradeService getTradeService() {
		return tradeService;
	}
	@Autowired
	public void setTradeService(TradeService tradeService) {
		this.tradeService = tradeService;
	}
	static {
		//tradeDB = new ConcurrentHashMap<>();
		prop = new Properties();
		try{
		InputStream input = new FileInputStream("src/main/resources/application.properties");
		prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} 

	}

}
