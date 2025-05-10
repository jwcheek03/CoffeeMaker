package edu.ncsu.csc.CoffeeMaker.common;

import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class for handy utils shared across all of the API tests
 *
 * @author Kai Presler-Marshall
 *
 */
public class TestUtils {

    // GENERATIVE AI WAS USED. Used ChatGPT. There were many prompts; too many
    // to list. Initial prompt was asking 'what does this mean' followed by a
    // stack trace. The issue involved LocalDateTime not being serializable
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter( LocalDateTime.class, new LocalDateTimeSerializer() ).create();

    /**
     * Uses Google's GSON parser to serialize a Java object to JSON. Useful for
     * creating JSON representations of our objects when calling API methods.
     *
     * @param obj
     *            to serialize to JSON
     * @return JSON string associated with object
     */
    public static String asJsonString ( final Object obj ) {
        return gson.toJson( obj );
    }

}
