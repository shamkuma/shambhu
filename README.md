# shambhu

Assignment Details:
The Problem Statement
 
The objective of this problem is to build a stock price caching service which will consume a stream of stock prices and will maintain an updated cache. It will receive the prices from various source in : <source, symbol, price> format and maintain a cache for consumers of this data. It will also expose a consumption API for clients to call and get the latest (and highest) price of a symbol. While storing the price it would check whether the price received has moved more than .1%, if not it will discard the update.
 
Since this can be called by multiple input sources it needs to be performant enough to deal with large amount of data. It should support simultaneous multiple callers.
 
At a very basic level this service should expose the following methods:
consumePrice( source, symbol, price) – this should receive and store the updated price per source/symbol combination.
getPrice(symbol) – this should retrieve the highest price for various source of a symbol and echo back to the client.

Code clone and setup
----------------------

Step1: Download or clone project from git hub
      git clone https://github.com/shamkuma/shambhu.git

Step2:Import Stock-Price-Caching-App into STS as "Existing Maven Project"

Step3: Do maven dependency update 
         Right Click on Project "Stock-Price-Caching-App" -> Maven -> Update Project

Step 4: Run as Spring Boot Application by Right Click on Project or Run as Java Application.

Running Junit
-------------	 

All testcase should execute successfully

Testing Using Browser
--------------------
Scenario One: When Junit has not been executed means no data exist on server
   Case1:
       Step 1: http://localhost:8080/getPrice/TCS
	          output : {"timestamp":1517243947443,"message":"Price not exist for Symbol:TCS","details":"uri=/getPrice/TCS"}


Case2: Provide data by browser 
     	Step1: http://localhost:8080/consumePrice?source=NSE&symbol=TCS&price=200
		        OutPut:Symbol/Source Price added successfully true
		Step 2: http://localhost:8080/getPrice/TCS
		       Output:Price for Symbol TCS is: 200.0
			   
Case3: When Price Diffrence more than .1%
         Step1:	http://localhost:8080/consumePrice?source=NSE&symbol=TCS&price=400
          Output:Symbol price updated(Price diffrence more than .1%)true
		  Step2: http://localhost:8080/getPrice/TCS
		  Output:Price for Symbol TCS is: 400.0
Case4: When Price Diffrence is less than .1%
       Step1: http://localhost:8080/consumePrice?source=NSE&symbol=TCS&price=400.001
		OutPut: Price Not Updated(Price diffrence less than .1%)false
		Step2: http://localhost:8080/getPrice/TCS
		  Output:Price for Symbol TCS is: 400.0
		
		  
