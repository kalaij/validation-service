package uk.ac.ebi.subs.validator.schema;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.config.MongoDBDependentTest;
import uk.ac.ebi.subs.validator.schema.custom.SchemaNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Category(MongoDBDependentTest.class) // Spring auto configuration creates the MongoDB client beans and tries to connect.
public class SchemaServiceTest {

    @Autowired
    private SchemaService schemaService;

    @Test
    public void getSchemaForSample() {
        System.out.println(schemaService.getSchemaFor(new Sample()));
    }

    @Test(expected = SchemaNotFoundException.class)
    public void getSchemaForStudy_throwsException() {
        schemaService.getSchemaFor(new Study());
    }

    @Test(expected = SchemaNotFoundException.class)
    public void getSchemaForAssay_throwsException() {
        schemaService.getSchemaFor(new Assay());
    }

    @Test(expected = SchemaNotFoundException.class)
    public void getSchemaForAssayData_throwsException() {
        schemaService.getSchemaFor(new AssayData());
    }
}
