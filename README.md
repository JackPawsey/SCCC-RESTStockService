# RESTStockService

The REST Stock Service caches external API responses into the directory “Cached Responses”

Here the response from each stock call is cached into a JSON file e.g. “AAPL-response.json”.

The client calls this service directly to retrieve relevant stock data. 

It exposes the “/queryAPI” endpoint as a HTTP GET method returning a JSON string. 

The service serves data from .JSON files that it creates using the response from external API calls

Every 5 minutes these local files are refreshed by calling the external API so that data served to the client is no more than 5 minutes old

Upon recieving a request the service checks if a cached response for the stock exists; if it does then that data is served otherwise the service makes the API call to retrieve and cache data for that stock before returning the response
