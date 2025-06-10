import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

//import org.apache.commons.io.IOUtils;
//import API;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
/*
 * The API_OpenWeatherMap class gets the overall weather (e.g. Clouds)
 * and temperature in Fahrenheit (e.g. 47.22) for a specified city.
 * For more information about the org.apache.commons.io.FileUtils.API itself, see "https://openweathermap.org/current".
 */
public class API_OpenWeatherMap extends API
{
    //Constructor
    public API_OpenWeatherMap() {
        this.key = "61dd4566f1feb50d8f0ac6ac6aa91620";
        this.name = "OpenWeatherMap";
    }

    // Get weather and temperature from org.apache.commons.io.FileUtils.API. Return true if successful. Otherwise, return false.
    public boolean call(String cityName) throws IOException, JSONException {

        // Store city name
        this.cityName = cityName;

        // Get URL. Connect to org.apache.commons.io.FileUtils.API using URL.
        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + "61dd4566f1feb50d8f0ac6ac6aa91620");
        connectToAPI(url, true);

        //Retry, if needed
        if(this.connection == null || this.connection.getResponseCode() != 200)
        {
            try {
                retry(url, true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // After retries.
        if(this.connection == null || this.connection.getResponseCode() != 200)
        {
            System.out.println("Unable to provide weather and temperature information for '" + this.cityName + "'");
            return false; // call failed
        }

        // Store "prettified" api response into file
        InputStream inputStream = connection.getInputStream();
        System.out.println("api CWD: " + this.api_cwd);
        File output = new File(this.api_cwd + "/output_OpenWeatherMap.json");
        
	if(!output.exists())
        {
            System.out.println("OUTPUT file does not exist yet");
            output.createNewFile();
        }
        transferInputStreamToFile(inputStream, output);
        System.out.println("Calling FileManager.makePretty(output)");
        output = FileManager.makePretty(output);

        //Get current weather and temperature, print message

        //message(getCurrentWeather(output), getCurrentTemperature(output));
	String weather = getCurrentWeather(output);
	double tempFahrenheit = getCurrentTemperature(output);
	message(weather, tempFahrenheit);
        return true;
    }

    // For chosen city, specify weather and temperature (in fahrenheit) to console.
    // Message includes temperature-based note about whether to bike or not.
    public void message(String weather, double fahrenheit)
    {
        System.out.println("It's " + weather.toLowerCase() + " weather today in " + this.cityName);
        System.out.printf("The temperature is %.2f degrees Fahrenheit. ", fahrenheit);
        if(fahrenheit < 40)
        {
            System.out.println("A bit cold for a bike ride...");
        }
        else if(fahrenheit > 80)
        {
            System.out.println("A bit hot for a bike ride...");
        }
        else { // 40 <= fahrenheit <= 80
            System.out.println("Nice temperature for a bike ride.");
        }
    }

    // Get overall weather (EX: clouds. EX: clear)
    public String getCurrentWeather(File prettyFile) throws JSONException
    {
        JSONObject json = new JSONObject(FileManager.storeFileAsString(prettyFile));
        JSONArray weather = json.getJSONArray("weather");
        JSONObject weather_= weather.getJSONObject(0);
        String result = weather_.get("main").toString(); //weather -> main
        return result;
    }

    // Returns double for current temperature in fahrenheit.
    public double getCurrentTemperature(File prettyFile) throws JSONException
    {
        JSONObject json = new JSONObject(FileManager.storeFileAsString(prettyFile));
        JSONObject main = json.getJSONObject("main");
        String temp = main.get("temp").toString(); // main --> temp
        double tempFahrenheit = getFahrenheitFromKelvin(temp);
        return tempFahrenheit; //temp manually to avoid rounding issues
    }

    // Given String temp (temperature in kelvin), returns corresponding temperature in Fahrenheit
    // NOTE: single line causes rounding issues --> split into multiple lines now
    public double getFahrenheitFromKelvin(String temp)
    {
        double tempKelvin = Double.valueOf(temp);
        double tempMinus273 = tempKelvin - 273.15;
        double tempMultiply = tempMinus273 * 1.8;
        double tempAdd = tempMultiply + 32;
        return tempAdd; // result
    }

}
