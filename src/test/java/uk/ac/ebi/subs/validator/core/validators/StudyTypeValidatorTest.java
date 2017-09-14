package uk.ac.ebi.subs.validator.core.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.ac.ebi.subs.data.component.StudyDataType;
import uk.ac.ebi.subs.data.component.StudyRef;
import uk.ac.ebi.subs.repository.model.Study;
import uk.ac.ebi.subs.validator.core.config.MongoDBDependentTest;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateSingleValidationResult;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateStudyRef;

@Category(MongoDBDependentTest.class)
public class StudyTypeValidatorTest {

    private StudyTypeValidator studyTypeValidator = new StudyTypeValidator();
    private Study study;

    @Before
    public void buildUp() {
        study = new Study();
    }

    @Test
    public void studyTypePresentSoPass() {
        study.setStudyType(StudyDataType.Metabolomics);

        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        studyTypeValidator.validate(study, singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Pending, singleValidationResult.getValidationStatus());
    }

    @Test
    public void studyTypeAbsentSoFail() {
        study.setStudyType(null);

        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        studyTypeValidator.validate(study, singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Error, singleValidationResult.getValidationStatus());
    }

}
