import com.google.gson.*;
import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

/*
 * The FileManager class allows access to and manipulation of files.
 *
 * Functionalities:
 *          - makePretty
 *          - storeFileAsString
 */
public class FileManager {

    /*
     * Takes the contents of an "ugly" file and makes a new file
     * where that content is stored in a way that looks "pretty" (formatted).
     * Returns "prettified" file.
     */
    public static File makePretty(File fileName) throws IOException {

        //read in file as string
        String uglyString = FileUtils.readFileToString(fileName);
        System.out.println("filename = " + fileName);

        //make "pretty" version of the string
        Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
        JsonElement je = JsonParser.parseString(uglyString);
        String prettyJsonString = gson.toJson(je);

        //write "pretty" text to new file
        File prettyFile = new File(fileName.getPath());
        FileUtils.writeStringToFile(prettyFile, prettyJsonString);

        return prettyFile;
    }

   //Store contents of prettyFile in a String called file_content
    // Returns file_content
    public static String storeFileAsString(File prettyFile)
    {
        String file_content = "";
        try {
            file_content = FileUtils.readFileToString(prettyFile);
        } catch (FileNotFoundException e) {
            System.out.println("Error: file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error: IOException.");
            e.printStackTrace();
        }
        return file_content;
    }
}
