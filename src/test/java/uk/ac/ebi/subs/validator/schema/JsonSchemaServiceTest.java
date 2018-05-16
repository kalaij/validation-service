package uk.ac.ebi.subs.validator.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.validator.config.MongoDBDependentTest;
import uk.ac.ebi.subs.validator.schema.model.JsonSchemaValidationError;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Category(MongoDBDependentTest.class) // Spring auto configuration creates the MongoDB client beans and tries to connect.
public class JsonSchemaServiceTest {

    @Autowired
    private JsonSchemaService jsonSchemaService;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void errorList_ShouldBe_Empty() throws IOException {
        List<JsonSchemaValidationError> errorList = jsonSchemaService.validate(mapper.readTree("{}"), mapper.readTree("{}"));
        assertThat(errorList, empty());
    }

    @Test
    public void errorList_ShouldHave_OneError() throws IOException {
        List<JsonSchemaValidationError> errorList = jsonSchemaService.validate(mapper.readTree("{\"required\": [ \"alias\" ]}"), mapper.readTree("{}"));
        assertThat(errorList, hasSize(1));
    }

    @Test
    public void errorList_ShouldHave_ErrorOnMissingAlias() throws IOException {
        List<JsonSchemaValidationError> errorList = jsonSchemaService.validate(mapper.readTree("{\"required\": [ \"alias\" ]}"), mapper.readTree("{}"));
        assertThat(errorList.get(0).getDataPath(), is("alias"));
    }

    @Test
    public void errorList_ShouldHave_OneErrorMessageOnMissingAlias() throws IOException {
        List<JsonSchemaValidationError> errorList = jsonSchemaService.validate(mapper.readTree("{\"required\": [ \"alias\" ]}"), mapper.readTree("{}"));
        assertThat(errorList.get(0).getErrors(), hasSize(1));
    }

    @Test
    public void errorList_ErrorMessageOnMissingAlias_ShouldBe() throws IOException {
        List<JsonSchemaValidationError> errorList = jsonSchemaService.validate(mapper.readTree("{\"required\": [ \"alias\" ]}"), mapper.readTree("{}"));
        assertThat(errorList.get(0).getErrors().get(0), is("should have required property 'alias'"));
    }
}
