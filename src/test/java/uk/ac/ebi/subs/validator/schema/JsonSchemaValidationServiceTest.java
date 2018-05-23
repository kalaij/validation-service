package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.config.MongoDBDependentTest;
import uk.ac.ebi.subs.validator.schema.model.JsonSchemaValidationError;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@Category(MongoDBDependentTest.class) // Spring auto configuration creates the MongoDB client beans and tries to connect.
public class JsonSchemaValidationServiceTest {

    @Autowired
    private JsonSchemaValidationService jsonSchemaValidationService;
    @Autowired
    private SchemaService schemaService;
    private static ObjectMapper mapper;

    private String sampleSchemaUrl = "https://raw.githubusercontent.com/EMBL-EBI-SUBS/validation-schemas/master/sample/sample-schema.json";

    @BeforeClass
    public static void setUp() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY); // Null fields and empty collections are not included in the serialization.
    }

    @Test
    public void errorList_ShouldBe_Empty() throws IOException {
        List<JsonSchemaValidationError> errorList = jsonSchemaValidationService.validate(mapper.readTree("{}"), mapper.readTree("{}"));
        assertThat(errorList, empty());
    }

    @Test
    public void errorList_ShouldHave_OneError() throws IOException {
        List<JsonSchemaValidationError> errorList = jsonSchemaValidationService.validate(mapper.readTree("{\"required\": [ \"alias\" ]}"), mapper.valueToTree(new Sample()));
        assertThat(errorList, hasSize(1));
    }

    @Test
    public void errorList_ShouldHave_ErrorOnMissingAlias() throws IOException {
        List<JsonSchemaValidationError> errorList = jsonSchemaValidationService.validate(mapper.readTree("{\"required\": [ \"alias\" ]}"),  mapper.valueToTree(new Sample()));
        assertThat(errorList.get(0).getDataPath(), is("alias"));
    }

    @Test
    public void errorList_ShouldHave_OneErrorMessageOnMissingAlias() throws IOException {
        List<JsonSchemaValidationError> errorList = jsonSchemaValidationService.validate(mapper.readTree("{\"required\": [ \"alias\" ]}"),  mapper.valueToTree(new Sample()));
        assertThat(errorList.get(0).getErrors(), hasSize(1));
    }

    @Test
    public void errorList_ErrorMessageOnMissingAlias_ShouldBe() throws IOException {
        List<JsonSchemaValidationError> errorList = jsonSchemaValidationService.validate(mapper.readTree("{\"required\": [ \"alias\" ]}"),  mapper.valueToTree(new Sample()));
        assertThat(errorList.get(0).getErrors().get(0), is("should have required property 'alias'"));
    }

    @Test
    public void errorList_shouldHave_ThreeErrors() throws IOException {
        JsonNode sampleSchema = schemaService.getSchemaFor(Sample.class.getTypeName(), sampleSchemaUrl);
        List<JsonSchemaValidationError> errorList = jsonSchemaValidationService.validate(sampleSchema, mapper.readTree("{}"));
        assertThat(errorList, hasSize(3));
    }

    @Test
    public void emptySample_hasSixErrors() {
        JsonNode sampleSchema = schemaService.getSchemaFor(Sample.class.getTypeName(), sampleSchemaUrl);
        List<JsonSchemaValidationError> errorList = jsonSchemaValidationService.validate(sampleSchema, mapper.valueToTree(new Sample()));
        assertThat(errorList, hasSize(3));
    }
}
