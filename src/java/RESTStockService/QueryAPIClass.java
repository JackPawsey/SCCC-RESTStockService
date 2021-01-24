/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RESTStockService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * REST Web Service
 *
 * @author Jack
 */
@Path("queryAPI")
public class QueryAPIClass {

    private static final String baseURL = "http://api.marketstack.com/v1/";
    private static final String path = "eod";
    private static final String apiKey = "?access_key=fcae76f06260b69b929a6dff81fd7cfb";

    /**
     * Retrieves representation of an instance of MarketCapRESTService.MarketCap
     * @param symbol
     * @param type
     * @return an instance of java.lang.String
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws java.text.ParseException
     * @throws org.json.simple.parser.ParseException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("symbol") String symbol, @QueryParam("type") String type) throws IOException, FileNotFoundException, ParseException, org.json.simple.parser.ParseException {
        String response = "REST service error";
        
        System.out.println("REQUEST: symbol: " + symbol + ", type: " + type);
        
        try {
            System.out.println("Reading file...");
            response = readJSONFile(symbol, type);
        } catch (IOException e) {
            System.out.println("File not found");
            
            try {
                System.out.println("Querying API...");
                response = queryRESTAPI(symbol, type);
            } catch (IOException x) {
                System.out.println("API error");
            }
        }
        
        return response;
    }
    
    // HTTP GET request
    private String queryRESTAPI(String symbol, String type) throws IOException, FileNotFoundException, ParseException, org.json.simple.parser.ParseException {
        URL obj = new URL(baseURL + path + apiKey + "&symbols=" + symbol);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        String inputLine;
        
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            System.out.println("GET request successful");
            
            processJSON(response);
            
            return readJSONFile(symbol, type);
        } else {
                System.out.println("GET request failed");
                return "REST service error";
        }
    }
    
    private static void processJSON(StringBuilder response) throws org.json.simple.parser.ParseException, IOException, FileNotFoundException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.toString());
        JSONObject jsonObject = (JSONObject) obj;

        JSONArray data = (JSONArray) jsonObject.get("data");

        JSONObject line1 = (JSONObject) data.get(0);
        
        writeJSONFile(line1, "C:\\Users\\Jack\\Documents\\NetBeansProjects\\RESTStockService\\Cached Responses\\" + line1.get("symbol").toString() + "-response.json");
    }
    
    //Write JSON file
    private static void writeJSONFile(JSONObject response, String fileName) throws FileNotFoundException, IOException, ParseException {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(response.toString());
            file.flush();

        } catch (IOException e) {
        }
    }
    
    // Load JSON data from file
    private String readJSONFile(String symbol, String type) throws org.json.simple.parser.ParseException, IOException {
        JSONParser parser = new JSONParser();

        Object obj = parser.parse(new FileReader("C:\\Users\\Jack\\Documents\\NetBeansProjects\\RESTStockService\\Cached Responses\\" + symbol + "-response.json"));

        JSONObject jsonObject = (JSONObject) obj;
        
        return round(jsonObject.get(type).toString());
    }
    
    private String round(String value) {
        Double d = new Double(value);
        BigDecimal bigdecimal = BigDecimal.valueOf(d);
        bigdecimal = bigdecimal.setScale(2, RoundingMode.HALF_UP);
        
        return bigdecimal.toString();
    }
    
    // HTTP GET request
    public static void refreshCache(String symbol) throws IOException, FileNotFoundException, ParseException, org.json.simple.parser.ParseException {
        URL obj = new URL(baseURL + path + apiKey + "&symbols=" + symbol);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        String inputLine;
        
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            System.out.println("Cache refreshed succesfully for " + symbol);
            
            processJSON(response);
        } else {
                System.out.println("Cache refresh failed for " + symbol);
        }
    }
    
    public static void getCachedStockSymbols() throws IOException, FileNotFoundException, ParseException, org.json.simple.parser.ParseException {
        File folder = new File("C:\\Users\\Jack\\Documents\\NetBeansProjects\\RESTStockService\\Cached Responses\\");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isFile()) {
  
            String[] splitStr = listOfFiles[i].getName().split("-");
            String symbol = splitStr[0].trim();
            
            refreshCache(symbol);
          }
        }
    }
}