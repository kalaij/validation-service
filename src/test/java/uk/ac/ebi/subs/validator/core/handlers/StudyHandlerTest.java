package uk.ac.ebi.subs.validator.core.handlers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.ProjectRef;
import uk.ac.ebi.subs.data.component.StudyDataType;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.core.validators.AttributeValidator;
import uk.ac.ebi.subs.validator.core.validators.ReferenceValidator;
import uk.ac.ebi.subs.validator.core.validators.StudyTypeValidator;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.List;

import static org.mockito.Mockito.when;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.commonTestMethodForEntities;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.fail;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.getValidationResultFromSubmittables;
import static uk.ac.ebi.subs.validator.core.handlers.ValidationTestHelper.pass;

@RunWith(SpringRunner.class)
public class StudyHandlerTest {

    private StudyHandler studyHandler;

    @MockBean
    private ReferenceValidator referenceValidator;

    @MockBean
    private AttributeValidator attributeValidator;

    @MockBean
    private StudyTypeValidator studyTypeValidator;

    private final String studyId = "studyId";
    private final String validationResultId = "vrID";
    private final int validationVersion = 42;
    private static final ValidationAuthor VALIDATION_AUTHOR_CORE = ValidationAuthor.Core;

    private StudyValidationMessageEnvelope envelope;

    private ProjectRef projectRef;
    private Submittable<Project> wrappedProject;

    private Study study;


    @Before
    public void buildUp() {
        //setup the handler
        studyHandler = new StudyHandler(studyTypeValidator, attributeValidator, referenceValidator);

        //refs
        projectRef = new ProjectRef();

        //entity to be validated
        study = new Study();
        study.setId(studyId);
        study.setProjectRef(projectRef);
        study.setStudyType(StudyDataType.Metabolomics);

        //reference data for the envelope
        Project project = new Project();
        String submissionId = "subID";
        wrappedProject = new Submittable<>(project, submissionId);

        //envelope
        envelope = new StudyValidationMessageEnvelope();
        envelope.setValidationResultUUID(validationResultId);
        envelope.setValidationResultVersion(validationVersion);
        envelope.setEntityToValidate(study);
        envelope.setProject(wrappedProject);
    }

    @Test
    public void testHandler_bothCallsPass() {
        mockValidatorCalls(pass(studyId, VALIDATION_AUTHOR_CORE), pass(studyId, VALIDATION_AUTHOR_CORE));

        List<SingleValidationResult> actualResults =
                commonTestMethodForEntities(getValidationResultFromSubmittables(studyHandler, envelope),
                        envelope, validationResultId, validationVersion, studyId, VALIDATION_AUTHOR_CORE);

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Pass, actualResults.get(0).getValidationStatus());
    }

    @Test
    public void testHandler_projectFails() {
        mockValidatorCalls(fail(studyId, VALIDATION_AUTHOR_CORE), pass(studyId, VALIDATION_AUTHOR_CORE));

        List<SingleValidationResult> actualResults =
                commonTestMethodForEntities(getValidationResultFromSubmittables(studyHandler, envelope),
                        envelope, validationResultId, validationVersion, studyId, VALIDATION_AUTHOR_CORE);

        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
    }

    @Test
    public void testHandler_studyTypeFails() {
        mockValidatorCalls(pass(studyId, VALIDATION_AUTHOR_CORE), fail(studyId, VALIDATION_AUTHOR_CORE));

        List<SingleValidationResult> actualResults =
                commonTestMethodForEntities(getValidationResultFromSubmittables(studyHandler, envelope),
                        envelope, validationResultId, validationVersion, studyId, VALIDATION_AUTHOR_CORE);

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(1, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
    }

    @Test
    public void testHandler_bothFail() {
        mockValidatorCalls(fail(studyId, VALIDATION_AUTHOR_CORE), fail(studyId, VALIDATION_AUTHOR_CORE));

        List<SingleValidationResult> actualResults =
                commonTestMethodForEntities(getValidationResultFromSubmittables(studyHandler, envelope),
                        envelope, validationResultId, validationVersion, studyId, VALIDATION_AUTHOR_CORE);

        //there should be one result (even though the handler received two passes) and it should be a pass
        Assert.assertEquals(2, actualResults.size());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(0).getValidationStatus());
        Assert.assertEquals(SingleValidationResultStatus.Error, actualResults.get(1).getValidationStatus());
    }

    private void mockValidatorCalls(SingleValidationResult projectResult, SingleValidationResult studyTypeResult) {
        when(
                referenceValidator.validate(studyId, projectRef, wrappedProject)
        ).thenReturn(
                projectResult
        );

        when(
                studyTypeValidator.validate(study)
        ).thenReturn(
                studyTypeResult
        );
    }
}
