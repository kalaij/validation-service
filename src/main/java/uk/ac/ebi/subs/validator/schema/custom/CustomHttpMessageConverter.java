package uk.ac.ebi.subs.validator.schema.custom;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;

/**
 * The GitHub raw endpoint serves the Content-Type as text/plain, for that reason, knowing that the endpoints
 * we will be calling are in fact returning a json response body we need a custom HttpMessageConverter that is in fact
 * the same as the MappingJackson2HttpMessageConverter but accepts Content-Type as text/plain.
 */
public class CustomHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public CustomHttpMessageConverter() {
        this.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN));
    }

}
