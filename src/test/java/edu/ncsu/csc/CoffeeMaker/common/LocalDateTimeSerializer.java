package edu.ncsu.csc.CoffeeMaker.common;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

// GENERATIVE AI WAS USED. Used ChatGPT. There were many prompts; too many
// to list. Initial prompt was asking 'what does this mean' followed by a
// stack trace. The issue involved LocalDateTime not being serializable
/**
 * Custom serializer for LocalDateTime objects to handle their serialization to
 * JSON.
 */
public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss" );

    @Override
    public JsonElement serialize ( final LocalDateTime localDateTime, final Type type,
            final JsonSerializationContext jsonSerializationContext ) {
        return new JsonPrimitive( formatter.format( localDateTime ) );
    }
}
