package com.stockApp.StockPriceCachingApp;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockPriceCachingAppApplicationTests {
	List<String> msg;
	@Test
	public void contextLoads() {
	}
	
	@Before
	public void setUp() throws Exception {
		Stream<String> stream = Files.lines(Paths.get("src/main/resources/static/xml-source/trade-source.xml"));
		 msg=stream.collect(Collectors.toList());
	}

	@Test
	public void test() {
		 Client client = Client.create();
		   WebResource webResource = client
				   .resource("http://localhost:8080/consumePrice/");
		for (String inputMsg : msg) {
			 ClientResponse response = webResource.type("application/xml")
					   .post(ClientResponse.class, inputMsg);
			 response = webResource.type("application/xml")
					 .post(ClientResponse.class, inputMsg);
		}
		   
	}
	
	@Test
	public void getPriceTest() {
		 Client client = Client.create();
		   WebResource webResource = client
				   .resource("http://localhost:8080/getPrice/INR");
		   ClientResponse response = webResource.type("plain/text")
				   .get(ClientResponse.class);
		   response = webResource.type("application/xml")
					 .get(ClientResponse.class);
		   assertEquals(200, response.getStatus());
	}
	
	@Test
	public void getPriceTestForINRSymbol() {
		 Client client = Client.create();
		   WebResource webResource = client
				   .resource("http://localhost:8080/getPrice/INR");
		   ClientResponse response = webResource.type("plain/text")
				   .get(ClientResponse.class);
		   response = webResource.type("plain/text")
					 .get(ClientResponse.class);
		  String value=response.getEntity(String.class);
		 String s[]=value.split(" ");
		 double d=Double.parseDouble(s[s.length-1]);
		
		assertEquals("85.0", s[s.length-1]);
		
	
		
	}


}
