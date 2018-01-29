package com.stockApp.StockPriceCachingApp;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StockPriceCachingAppApplicationTests {
	List<String> msg;
	
	
	@Before
	public void setUp() throws Exception {
		Stream<String> stream = Files.lines(Paths.get("src/main/resources/static/xml-source/trade-source.xml"));
		 msg=stream.collect(Collectors.toList());
		 Client client = Client.create();
		   WebResource webResource = client
				   .resource("http://localhost:8080/consumePrice/");
		for (String inputMsg : msg) {
			  webResource.type("application/xml")
					   .post(ClientResponse.class, inputMsg);
			 
		}
		stream.close();
	}

	
	@Test
	public void Test1() {
		 Client client = Client.create();
		   WebResource webResource = client
				   .resource("http://localhost:8080/getPrice/TCS");
		   ClientResponse response = webResource.type("plain/text")
				   .get(ClientResponse.class);
		   response = webResource.type("application/xml")
					 .get(ClientResponse.class);
		   assertEquals(200, response.getStatus());
	}
	
	@Test
	public void Test2() {
		 Client client = Client.create();
		   WebResource webResource = client
				   .resource("http://localhost:8080/getPrice/TCS");
		   ClientResponse response = webResource.type("plain/text")
				   .get(ClientResponse.class);
		   response = webResource.type("plain/text")
					 .get(ClientResponse.class);
		  String value=response.getEntity(String.class);
		 String s[]=value.split(" ");
		 
		
		assertEquals("65.0", s[s.length-1]);
		
	}
	
	//Testcase for Price update of "TCS" symbol when difference is more than .1%
	@Test
	public void Test3() {
		Client client = Client.create();
		   WebResource webResource = client
				   .resource("http://localhost:8080/consumePrice/");
		   String inputMsg="<Trade><symbol>TCS</symbol><source>BSE</source><price>100</price></Trade>";
		
			  webResource.type("application/xml")
					   .post(ClientResponse.class, inputMsg);
			   webResource = client
					   .resource("http://localhost:8080/getPrice/TCS");
			   ClientResponse response = webResource.type("plain/text")
					   .get(ClientResponse.class);
			   response = webResource.type("plain/text")
						 .get(ClientResponse.class);
			  String value=response.getEntity(String.class);
			 String s[]=value.split(" ");
			 
			
			assertEquals("100.0", s[s.length-1]);
		
	}
	


}
