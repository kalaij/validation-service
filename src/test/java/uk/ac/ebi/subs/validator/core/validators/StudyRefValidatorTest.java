package uk.ac.ebi.subs.validator.core.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.StudyRef;
import uk.ac.ebi.subs.repository.model.Study;
import uk.ac.ebi.subs.repository.repos.submittables.StudyRepository;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateSingleValidationResult;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateStudyRef;

public class StudyRefValidatorTest {

    private StudyRefValidator studyRefValidator;
    private StudyRepository studyRepository;

    @Before
    public void setUp()  {
        studyRefValidator = new StudyRefValidator();
        studyRepository = mock(StudyRepository.class);
        studyRefValidator.studyRepository = studyRepository;

        when(studyRepository.findFirstByAccessionOrderByCreatedDateDesc("STUDY0001")).thenReturn(new Study());
        when(studyRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc("testTeam", "testAlias")).thenReturn(new Study());

    }

    @Test
    public void studyRefNotFoundEmptyAccessionTest() {
        StudyRef studyRef = generateStudyRef("", "testTeam", "testAlias");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        studyRefValidator.validate(studyRef, singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Error, singleValidationResult.getValidationStatus());
    }

    @Test
    public void studyRefFoundTest() {
        StudyRef studyRef = generateStudyRef("STUDY0001");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        studyRefValidator.validate(studyRef, singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Pass, singleValidationResult.getValidationStatus());
    }

    @Test
    public void studyRefFoundByTeamAndAliasTest() {
        StudyRef studyRef = generateStudyRef("", "testTeam", "testAlias");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        studyRefValidator.validate(studyRef, singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Pass, singleValidationResult.getValidationStatus());
    }

}
