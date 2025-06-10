import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONException;
/*
 * The API_AirQuality class gets the air quality for a specified city.
 * For more information about the API itself, see "https://api-ninjas.com/api/airquality"
 */
public class API_AirQuality extends API {
    
    // Constructor
    public API_AirQuality() {
        this.key = "ZR493VmPvcM6d0zysg2+LQ==cRw8OtDNOdbWbgDy";
        this.name = "Air Quality (API Ninjas)";
        this.base_url = "https://api.api-ninjas.com/v1/airquality?city=";
    }

    // Implementation of abstract call method from API
    // Return true if able to get cityName's air quality from the API. 
    // Otherwise, return false.
    public boolean call(String cityName) throws IOException, JSONException {

        // Store city name
        this.cityName = cityName;

        //Adjust city name to not have spaces, as applicable (replace with +)
        this.cityName = getCityNameReplaceSpaceWithPlus();
        
        // Get URL. Connect to API using URL.
        //System.out.println("\n-------------------------\n");
        String base_url_city = this.base_url + this.cityName.toLowerCase();
        URL url = new URL(base_url_city);
        //System.out.println("url = " + url);
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
            System.out.println("Unable to provide air quality information for '" + this.cityName + "'");
            return false; // call failed
        }
        // Store "prettified" api response into file
        InputStream inputStream = connection.getInputStream();
        File output = new File(this.api_cwd + "/output_AirQuality.json"); 
        transferInputStreamToFile(inputStream, output);
        output = FileManager.makePretty(output); //careful...
        
        // Get aqi, print message
        int aqi = getOverallAirQuality(output);
        message(aqi);
        
        return true; // call did not fail
    }


    // Given prettyFile holding data from successful GET request, returns int overall_aqi.
    // EXPECT: non-negative overall_aqi
    public int getOverallAirQuality(File prettyFile) throws JSONException
    {
        JSONObject json = new JSONObject(FileManager.storeFileAsString(prettyFile)); // CO, NO2, O3, SO2, PM2.5, PM10,  overall_aqi
        int overall_aqi = (int) json.get("overall_aqi");
        return overall_aqi;
    }

    // Print aqi value and print comment on biking-friendliness of the current aqi value.
    public void message(int aqi)
    {
            String aqi_category = getAQI_Category(aqi);
            System.out.println("Air Quality Index (AQI) = " + aqi + " -- " + aqi_category);
            if(aqi >= 0 && aqi <= 50) // Good
            {
                System.out.println("Lots of fresh air! Great air quality for a bike ride!");
            }
            else if(aqi <= 100) // Moderate
            {
                System.out.println("Decent air quality for a bike ride.");
            }
            else if(aqi <= 150) // Unhealthy for sensitive groups
            {
                System.out.println("Sensitive groups might want to bike ride another day...");
            }
            else { // aqi > 150 ==> Unhealthy, Very Unhealthy, Hazardous
                System.out.println("For your health, everyone please refrain from taking a bike ride!");
            }
            

    }

    // Given int aqi, return String aqi category.
    // Example: 35 would return "Good".
    //
    // Resource: categories match those of https://www.iqair.com/us/world-air-quality-ranking
    //
    // Categories:
    //  [0,50]:     Good
    //  [51, 100]:  Moderate
    //  [101, 150]: Unhealthy for sensitive groups
    //  [151, 200]: Unhealthy
    //  [201, 300]: Very Unhealthy
    //  [301, inf]: Hazardous
    public String getAQI_Category(int aqi)
    {
        if(aqi < 0)
        {
            System.out.println("Error: Unexpected aqi (aqi < 0)");
            return null; //address this...
        }
        else if(aqi <= 50) { return "Good"; }
        else if(aqi <= 100) { return "Moderate"; }
        else if(aqi <= 150) { return "Unhealthy for sensitive groups"; }
        else if(aqi <= 200) { return "Unhealthy"; }
        else if(aqi <= 300) { return "Very Unhealthy"; }
        else { return "Hazardous"; } //aqi > 300 
    }
}
