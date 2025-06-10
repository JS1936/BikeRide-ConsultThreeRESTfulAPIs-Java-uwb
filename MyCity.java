import java.util.Vector;
import java.io.IOException;
import java.util.Scanner;
import org.json.JSONException;
public class MyCity
{
	static Vector<API> apis = new Vector<API>();

	// Expect: capital first-letter. If multiple words, surround with quotation marks.
	// Examples: Chicago, "New York"
	public static void main(String[] args) throws IOException, JSONException
	{
		// Check number of arguments	
		if(args.length != 1)
		{
			System.out.println("Error: expected only one argument (cityName)");
			return;
		}

		// Correct number of arguments. Continue.
		welcomeUser();
        	initializeAPIs();
        	printAPIs();
        	String cityName = args[0];//promptUserForCityName();
        	getCityInformation(cityName); // consults each api in apis
	}


	// Prints brief welcome message to console.
    	public static void welcomeUser()
    	{
        	System.out.println("--------------------------------------------");
       		System.out.println("            Welcome to Project 2            ");
       		System.out.println();
        	System.out.println();
        	System.out.println("                   Scenario:                ");
        	System.out.println("        'Should I go bike riding today?'");
        	System.out.println("--------------------------------------------");
    	}

	// No longer used.
	// Was previously used for testing/debugging.
    	public static String promptUserForCityName()
    	{
        	String cityName = "<default city name>";

        	Scanner input = new Scanner(System.in);
        	System.out.println("Please type the name of the city in which you want to ride your bike.");
        	System.out.println("Format: Capital first letter. 1 space between words.  EX: Seattle  EX: \"New Orleans\"");

        	System.out.print("\nenter city name: ");
        	cityName = input.nextLine();
        	System.out.println("City Name = " + cityName);
        	input.close();

        	return cityName;
   	 }

    	// Initialize the desired APIs. 
	// To stop calling a certain API, uncomment the constructor and the add call.
    	public static void initializeAPIs() throws JSONException
    	{
        	API openWeatherMapAPI = new API_OpenWeatherMap();
        	API bikesAPI = new API_Bikes();
        	API airQuality = new API_AirQuality();

        	//apis.clear();
        	apis.add(openWeatherMapAPI);  // seems okay with spaces
        	apis.add(bikesAPI);           // seems okay with spaces
        	apis.add(airQuality);         // not okay with spaces --> gets adjusted in API_AirQuality.java
    	}

    	// Prints the String names of the APIs to consult.
    	public static void printAPIs()
    	{
        	System.out.println("\t\tAPIs in use:\n");
        	int count = 1;
        	for(API api : apis)
        	{
            		System.out.println("\t" + count + ": " + api.name);
            		count++;
        	}
        	System.out.println("--------------------------------------------");
    	}

    	// Gets the relevant city information from each org.apache.commons.io.FileUtils.API and prints it to console.
    	public static void getCityInformation(String cityName) throws IOException, JSONException {
        	System.out.println("---------------------------------");
        	for(API api : apis)
        	{
            		api.call(cityName);
            		System.out.println("---------------------------------");
        	}
    	}
}
