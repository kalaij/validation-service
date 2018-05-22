package uk.ac.ebi.subs.validator.schema;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;

public class SchemaServiceTest {

    private SchemaService schemaService = new SchemaService();

    @Test(expected = SchemaNotFoundException.class)
    public void getSchemaForSample_throwsException() {
        schemaService.getSchemaFor(new Sample());
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
