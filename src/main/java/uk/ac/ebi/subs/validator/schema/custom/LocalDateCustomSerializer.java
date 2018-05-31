package uk.ac.ebi.subs.validator.schema.custom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;

/**
 * This serializer converts a LocalDate to a simple string with the format YYYY-MM-DD
 */
public class LocalDateCustomSerializer extends StdSerializer<LocalDate> {

    public LocalDateCustomSerializer() {
        this(null);
    }

    public LocalDateCustomSerializer(Class<LocalDate> t) {
        super(t);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getYear() + "-" + String.format("%02d", value.getMonthValue()) + "-" + String.format("%02d", value.getDayOfMonth()));
    }
}