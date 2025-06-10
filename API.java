import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import java.util.concurrent.TimeUnit;

abstract public class API {
    protected String key = "<key>";
    protected String name = "<cityName>";
    protected String base_url = "<base_url>";
    protected HttpURLConnection connection = null;
    protected String cityName = "<cityName>";
    protected String api_cwd = System.getProperty("user.dir");

    // Implemented by each specific API used. 
    // Given String cityName, tries to connect, get data, and print relevant message.
    // Returns true if connection was successful. Otherwise, returns false.
    // Connection does not guarantee data on that city was available.
    // If no data on that city, prints corresponding message to console. 
    abstract boolean call(String cityName) throws IOException, JSONException;

    // Ends program if null connection.
    protected void endProgramIfNullConnection() {
        if(connection == null) // no connection
        {
            System.out.println("Error: connection is null");
            System.exit(0);
        }
    }

    // Helpful for debugging. Prints to console whether response code was 200 or not.
    // Also returns the response code. (temp?)
    protected int printResponseCodeSuccessFail(URL url) throws IOException {
        int responseCode = this.connection.getResponseCode(); // expect: 200
        //System.out.println("Response code: " + responseCode); //expect: 200
        if(responseCode == 200) //response is valid/OK
        {
            //System.out.println("Connection made. URL: " + url.toString()); // Helpful for debugging
        }
        else
        {
            System.out.println("Error: connection to api has invalid response (response code = " + responseCode + ")");
        }
        return responseCode;
    }

    // Attempt to connect to url. Returns null if no connection, otherwise returns connection.
    protected HttpURLConnection connectToAPI(URL url, boolean hasKey) throws IOException {

        this.connection = (HttpURLConnection) url.openConnection();
        endProgramIfNullConnection();
        connection.setRequestMethod("GET");
        if(hasKey)
        {
            connection.setRequestProperty("Authorization","Bearer " + this.key);
            connection.setRequestProperty("x-api-key",this.key); // for air quality api

        }
        connection.setRequestProperty("Accept", "application/vnd.api+json");

        printResponseCodeSuccessFail(url);
        return this.connection;
    }

    //Pre: File file exists and is not null.
    //Transfers desired content from input stream into given file.
    protected void transferInputStreamToFile(InputStream inputStream, File file) throws IOException
    {
        OutputStream output = new FileOutputStream(file);
        inputStream.transferTo(output);
        inputStream.close();
        output.close();
    }

    // Retry connecting to API. Uses exponential backoff with maximum 3 retries.
    // During 1st retry, waits 1 sec (2^0)
    // During 2nd retry, waits 2 sec (2^1)
    // During 3rd retry, waits 4 sec (2^2)
    // Limit: 3 retries.
    protected void retry(URL url, boolean hasKey) throws InterruptedException, IOException
    {
        int wait = 1;
        while(wait <= 4 && connection.getResponseCode() != 200)
        {
            System.out.println("Retry (" + wait + "sec wait)...");
            // wait
            TimeUnit.SECONDS.sleep(wait);

            // retry
            connectToAPI(url, hasKey);

            //update wait time
            wait *= 2;
        }
    }

    // Returns String result, which is this.cityName with each " " replaced as "+".
    protected String getCityNameReplaceSpaceWithPlus()
    {
        String result = this.cityName;
        if(result.contains(" "))
        {
            for(int i = 0; i < result.length(); i++)
            {
                char ch = result.charAt(i);
                if(ch == ' ')
                {
                    result = result.substring(0, i) + '+' + result.substring(i+1);
                }
            }
        }

        // Helpful for debugging:
        //System.out.println("city name = " + this.cityName);
        //System.out.println("result    = " + result);
        return result;
    }
}
