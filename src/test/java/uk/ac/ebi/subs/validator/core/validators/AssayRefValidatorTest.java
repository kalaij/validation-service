package uk.ac.ebi.subs.validator.core.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.ac.ebi.subs.data.component.AssayRef;
import uk.ac.ebi.subs.repository.model.Assay;
import uk.ac.ebi.subs.repository.repos.submittables.AssayRepository;
import uk.ac.ebi.subs.validator.core.config.MongoDBDependentTest;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateAssayRef;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateSingleValidationResult;

@Category(MongoDBDependentTest.class)
public class AssayRefValidatorTest {

    private AssayRefValidator assayRefValidator;
    private AssayRepository assayRepository;

    @Before
    public void setUp() {
        assayRefValidator = new AssayRefValidator();
        assayRepository = mock(AssayRepository.class);
        assayRefValidator.assayRepository = assayRepository;

        when(assayRepository.findFirstByAccessionOrderByCreatedDateDesc("ASSAY1234")).thenReturn(new Assay());
        when(assayRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc("testTeam", "testAlias")).thenReturn(null);
        when(assayRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc("Team", "Alias")).thenReturn(new Assay());
    }

    @Test
    public void assayFoundTest() {
        AssayRef assayRef = generateAssayRef("ASSAY1234");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        assayRefValidator.validate(assayRef, singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(SingleValidationResultStatus.Pass, singleValidationResult.getValidationStatus());
    }

    @Test
    public void assayNotFoundTest() {
        AssayRef assayRef = generateAssayRef(null, "testTeam", "testAlias");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        assayRefValidator.validate(assayRef, singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(SingleValidationResultStatus.Error, singleValidationResult.getValidationStatus());
    }

    @Test
    public void assayFoundByTeamAndAliasTest() {
        AssayRef assayRef = generateAssayRef("", "Team", "Alias");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        assayRefValidator.validate(assayRef, singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(SingleValidationResultStatus.Pass, singleValidationResult.getValidationStatus());
    }
}
