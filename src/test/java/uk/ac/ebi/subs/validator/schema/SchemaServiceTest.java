package uk.ac.ebi.subs.validator.schema;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.schema.custom.CustomHttpMessageConverter;
import uk.ac.ebi.subs.validator.schema.custom.SchemaNotFoundException;

import java.util.Arrays;

public class SchemaServiceTest {

    private String schemaUrl = "https://raw.githubusercontent.com/EMBL-EBI-SUBS/validation-schemas/master/study/study-schema.json";
    private RestTemplate restTemplate = new RestTemplate();
    private SchemaService schemaService;

    @Before
    public void setUp() {
        restTemplate.setMessageConverters(Arrays.asList(new CustomHttpMessageConverter()));
        schemaService = new SchemaService(restTemplate);
    }

    @Test
    public void getSchemaForSample() {
        schemaService.getSchemaFor(Sample.class.getTypeName(),"https://raw.githubusercontent.com/EMBL-EBI-SUBS/validation-schemas/master/sample/sample-schema.json");
    }

    @Test(expected = SchemaNotFoundException.class)
    public void getSchemaForStudy() {
        schemaService.getSchemaFor(Study.class.getTypeName(), schemaUrl);
    }

    @Test(expected = SchemaNotFoundException.class)
    public void getSchemaForAssay() {
        schemaService.getSchemaFor(Assay.class.getTypeName(), schemaUrl);
    }

    @Test(expected = SchemaNotFoundException.class)
    public void getSchemaForAssayData() {
        schemaService.getSchemaFor(AssayData.class.getTypeName(), schemaUrl);
    }
}
