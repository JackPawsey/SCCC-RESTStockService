/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RESTStockService;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import static RESTStockService.QueryAPIClass.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author Jack
 */
@Singleton
public class TimerClass {
    @Schedule(hour = "*", minute = "*/5", persistent = false) // Every 5 minutes
    public void doWork() throws IOException, FileNotFoundException, ParseException, org.json.simple.parser.ParseException{
        getCachedStockSymbols();
    }
}
