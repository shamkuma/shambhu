package com.stockApp.StockPriceCachingApp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.trade.controllers.TradeController;
import com.trade.model.Trade;
import com.trade.service.TradeService;
import com.trade.service.TradeServiceImpl;

import junit.framework.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StockPriceCachingAppApplicationTests {
	TradeServiceImpl tradeService;
	public TradeServiceImpl getTradeService() {
		return tradeService;
	}
	@Autowired
	public void setTradeService(TradeServiceImpl tradeService) {
		this.tradeService = tradeService;
	}
	
  @Test
  public void test0_tradeServiceNotNullTest(){
	  assertNotNull(tradeService);
  }

  @Test
  public void test1_loadDataInCacheMemory(){
	  List<Trade> list1=new ArrayList();
		list1.add(new Trade("TCS","NSE",100.0));
		list1.add(new Trade("TCS","BSE",200.0));
		List<Trade> list2=new ArrayList();
		list2.add(new Trade("INFY","NSE",900.0));
		list2.add(new Trade("INFY","BSE",950.0));
		List<Trade> list3=new ArrayList();
		list3.add(new Trade("CTS","NSE",1900.0));
		list3.add(new Trade("CTS","BSE",1950.0));
		//TradeServiceImpl tradeServiceImpl=(TradeServiceImpl)tradeService;
		tradeService.getTradeDB().put("TCS", list1);
		tradeService.getTradeDB().put("INFY", list2);
		tradeService.getTradeDB().put("CTS", list3);
  }
  
  
  @Test
  public void test2_insertInDBTest() {
	 Trade trade= new Trade("TCS","NSE",500.0);
	  tradeService.insertInDB(trade);
	 assertEquals(500.0, tradeService.getHighestPrice("TCS"),0.001);
	  
  }
  @Test
  public void test3_updateInDBTest() {
	  Trade trade= new Trade("CTS","NSE",1910.0);
	  tradeService.updateInDB(trade);
	   assertEquals(1910.0, tradeService.getPriceForSource(trade),0.001);
  }
  @Test
  public void test4_getHighestPriceTest() {
	  assertEquals(1950.0, tradeService.getHighestPrice("CTS"),0.001);
  }
	
  @Test
  public void test5_getPriceDiffrencePercentageTest() {
	  
	  assertEquals(true,tradeService.checkPriceDiffrencePercentage(100,101));
  }
  
  @Test
  public void test6_getPriceForSourceTest() {
	  Trade trade= new Trade("TCS","NSE",500.0);
	  assertEquals(500.0,tradeService.getPriceForSource(trade),0.001);
  }
  @Test
  public void test7_getPriceTradeControllerTest() {
	  TradeController tradeC=new TradeController();
	  System.out.println(tradeC.getPrice("TCS"));
  }
	
	
}
