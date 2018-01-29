package com.controllers;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.exception.PriceNotFoundException;
import com.model.Response;
import com.model.Trade;


@RestController
public class TradeController {
	
	private static Map<String, List<Trade>> tradeDB=new ConcurrentHashMap<>();
	private static Properties prop;
	private static InputStream input;
	
	
	
	@RequestMapping(value="/consumePrice",method = RequestMethod.POST, consumes ={MediaType.APPLICATION_XML}) 
	@Produces(MediaType.APPLICATION_XML)
	public @ResponseBody Response consumePrice(@RequestBody Trade t) {
		
		List<Trade> existingList = tradeDB.get(t.getSymbol());
		Response response = new Response();
		if (existingList == null || existingList.isEmpty()) {
			insertInDB(t.getSymbol(), t.getSource(), t.getPrice());
			response.setStatus(true);
			response.setMessage(prop.getProperty("success_msg1"));
			return response;
		} else {
			// check this source present for this symbol
			if (checkSourcePresence(existingList, t.getSource())) {
				double oldPrice = getPriceForSource(existingList, t.getSource());

				if (checkPriceDiffrence(oldPrice, t.getPrice())) {
					List<Trade> t1 = updatePrice(existingList, t.getSource(), t.getPrice());
					tradeDB.put(t.getSymbol(), t1);
					response.setStatus(true);
					response.setMessage(prop.getProperty("success_msg2"));
					return response;
					
				}else {
					response.setStatus(false);
					response.setMessage(prop.getProperty("error_msg1"));
					return response;
				}
			} else {
				insertInDB(t.getSymbol(), t.getSource(), t.getPrice());
				response.setStatus(true);
				response.setMessage(prop.getProperty("success_msg3"));
				return response;
			}

		}

		
	}

	@RequestMapping(value="/consumePrice",method = RequestMethod.GET) 
	@Produces(MediaType.APPLICATION_XML)
	public @ResponseBody Response consumePrice(@RequestParam("source") String source, @RequestParam("symbol") String symbol, @RequestParam("price") double price) {
		
		Trade t=getTradeObject(symbol, source, price);
		
		
		List<Trade> existingList = tradeDB.get(t.getSymbol());
		Response response = new Response();
		if (existingList == null || existingList.isEmpty()) {
			insertInDB(t.getSymbol(), t.getSource(), t.getPrice());
			response.setStatus(true);
			response.setMessage(prop.getProperty("success_msg1"));
			return response;
		} else {
			// check this source present for this symbol
			if (checkSourcePresence(existingList, t.getSource())) {
				double oldPrice = getPriceForSource(existingList, t.getSource());

				if (checkPriceDiffrence(oldPrice, t.getPrice())) {
					List<Trade> t1 = updatePrice(existingList, t.getSource(), t.getPrice());
					tradeDB.put(t.getSymbol(), t1);
					response.setStatus(true);
					response.setMessage(prop.getProperty("success_msg2"));
					return response; 
				}else {
					response.setStatus(false);
					response.setMessage(prop.getProperty("error_msg1"));
					return response;
				}
			} else {
				insertInDB(t.getSymbol(), t.getSource(), t.getPrice());
				response.setStatus(true);
				response.setMessage(prop.getProperty("success_msg2"));
				return response;
			}

		}

		//return response;
	}


	@RequestMapping(value="/getPrice/{symbol}",method = RequestMethod.GET)  
	@Produces(MediaType.TEXT_PLAIN)
	public String getPrice(@PathVariable String symbol) {
		String msg = null;
		List<Trade> existingList = tradeDB.get(symbol);
		if (existingList == null || existingList.isEmpty()) {
			msg =prop.getProperty("error_msg2")+ symbol;
		  throw new PriceNotFoundException(msg);
			
			//return msg;
		}

		existingList.sort((Trade t1, Trade t2) -> t1.getPrice().compareTo(t2.getPrice()));
		double price = existingList.get(existingList.size() - 1).getPrice();
		msg = prop.getProperty("message") + symbol + " is: " + price;
	

		return msg;
	}

	// function to construct object from parameter
	private Trade getTradeObject(String symbol, String source, double price) {
		Trade newTrade = new Trade();
		newTrade.setSymbol(symbol);
		newTrade.setSource(source);
		newTrade.setPrice(price);
		return newTrade;
	}

	// get the price for an existing source of a particular symbol
	private double getPriceForSource(List<Trade> existingList, String source) {

		List<Trade> filteredList = existingList.stream().filter(t -> t.getSource().equals(source))
				.collect(Collectors.toList());
		return filteredList.get(0).getPrice();
	}

	// Insert the record in database
	private void insertInDB(String symbol, String source, double price) {
		Trade newTrade = getTradeObject(symbol, source, price);

		List<Trade> list = tradeDB.get(symbol);
		if (list == null || list.isEmpty())
			list = new ArrayList<Trade>();
		list.add(newTrade);
		tradeDB.put(symbol, list);
	}

	// check if source for the particular Symbol already present in the database
	private boolean checkSourcePresence(List<Trade> existingList, String source) {

		List<Trade> filteredList = existingList.stream().filter(t -> t.getSource().equals(source))
				.collect(Collectors.toList());
		if (filteredList.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	// logic to compare .1% price difference
	private boolean checkPriceDiffrence(double oldPrice, double newPrice) {
		double percentage = 0;
		if (oldPrice < newPrice) {
			percentage = ((newPrice - oldPrice) * 100) / newPrice;

		} else {
			percentage = ((oldPrice - newPrice) * 100) / oldPrice;
		}
		if (percentage > 0.1)
			return true;
		else
			return false;
	}

	// update price if price difference is more than .1%
	private List<Trade> updatePrice(List<Trade> existingList, String source, Double newPrice) {
		List<Trade> t1 = existingList.stream().filter(t -> t.getSource().equals(source))
				.collect(Collectors.toList());
		t1.get(0).setPrice(newPrice);
		return existingList;
	}
	static {
		//tradeDB = new ConcurrentHashMap<>();
		prop = new Properties();
		try{
		input = new FileInputStream("src/main/resources/application.properties");
		prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} 

	}

}

