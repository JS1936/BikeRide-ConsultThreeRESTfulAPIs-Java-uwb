import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/*
 * The API_Bikes class gets (if possible) the name of a nearby citybike company for a specified city.
 * For more information about the API itself, see project page "http://api.citybik.es/v2/"
 */
public class API_Bikes extends API {

    // Constructor
    public API_Bikes() {
        this.key = null;
        this.name = "CityBikes";
        this.base_url = "http://api.citybik.es/v2/networks";
    }

    // Implementation of abstract call method from API
    // Returns true if call was successful. Returns false if call failed.
    public boolean call(String cityName) throws IOException, JSONException {

        // Store city name. Make sure first letter is upper-case for this api
        this.cityName = cityName.substring(0,1).toUpperCase() + cityName.substring(1);

        // Get URL. Connect to API using URL.
        URL url = new URL(this.base_url);
        connectToAPI(url, false);
        
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
            System.out.println("Unable to provide citybike information for '" + this.cityName + "'");
            return false; // call failed
        }
        
        // Store "prettified" api response into file
        // Note: <cwd>/output_Bikes.json is more flexible than hardcoding absolute path 
        InputStream inputStream = connection.getInputStream();
        File output = new File(this.api_cwd + "/output_Bikes.json"); 
        transferInputStreamToFile(inputStream, output);
        output = FileManager.makePretty(output); //careful...
        
        // Get nearby bike company (if possible), print message
        getNearbyBikeCompany(output);
        
        return true;
    }

    // If person can get a city bike in the specified city, return results as String.
    // Pre: cityName's first letter is capitalized
     public String getNearbyBikeCompany(File prettyFile) throws JSONException
    {
        JSONObject json = new JSONObject(FileManager.storeFileAsString(prettyFile));
   
        // Networks array —> company array —> for each in array, check location->city ==> does it match?
        // If does match, get networks->(same index)->name
        JSONArray networks = json.getJSONArray("networks"); // EX: length of 769
        boolean hasCity = false;
        String company = null;
        
        // For each option, check if the city matches
        for (int i = 0; i < networks.length(); i++)
        {
           
            JSONObject networks_ = networks.getJSONObject(i); // EX: length of 5
            company = networks_.get("company").toString();
            JSONObject location = networks_.getJSONObject("location");
            String city = location.get("city").toString();
            if(city.equalsIgnoreCase(this.cityName) || 
                city.contains(this.cityName + ",")) // in case of state...
            {
                System.out.println("city = " + city);
                i = networks.length(); // stop searching
                hasCity = true;
            }
        }

        if(hasCity == false)
        {
            System.out.println("No city bike rental companies found near " + this.cityName);
        }
        else
        {
            System.out.println("To rent a city bike, try visiting " + company + "!");
        }
        return company;
    }
}

