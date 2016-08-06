package ca.deca.decaontarioapp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Reno on 14-08-30.
 */
public class NetworkUtils {

    /**
     * Reads the contents of an InputStream and converts it to a String.
     *
     *
     * @param stream {InputStream}
     * @return {String}
     * @throws IOException
     */
    public static String readIt(InputStream stream) throws IOException {
        return new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
    }
}
