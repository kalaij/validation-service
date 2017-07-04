package uk.ac.ebi.subs.validator.core.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.repository.model.Sample;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateSampleRef;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateSampleRelationship;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateSampleUse;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.generateSingleValidationResult;

public class SampleRefValidatorTest {

    private SampleRefValidator sampleRefValidator;
    private SampleRepository sampleRepository;

    @Before
    public void setUp() {
        sampleRefValidator = new SampleRefValidator();
        sampleRepository = mock(SampleRepository.class);
        sampleRefValidator.sampleRepository = sampleRepository;

        when(sampleRepository.findByAccession("SAMEA100001")).thenReturn(null);
        when(sampleRepository.findByAccession("SAMEA123456")).thenReturn(new Sample());
        when(sampleRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc("testTeam", "testAlias")).thenReturn(new Sample());
    }

    @Test
    public void sampleRelationshipNotFoundTest() {
        SampleRelationship relationship = generateSampleRelationship("SAMEA100001");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        sampleRefValidator.validateSampleRelationships(Arrays.asList(relationship), singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Error, singleValidationResult.getValidationStatus());
    }

    @Test
    public void sampleRelationshipFoundTest() {
        SampleRelationship relationship = generateSampleRelationship("SAMEA123456");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        sampleRefValidator.validateSampleRelationships(Arrays.asList(relationship), singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Pass, singleValidationResult.getValidationStatus());
    }

    @Test
    public void sampleRefNotFoundTest() {
        SampleRef sampleRef = generateSampleRef(null);
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        sampleRefValidator.validate(sampleRef, singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Error, singleValidationResult.getValidationStatus());
    }

    @Test
    public void sampleRefFoundByTeamAndAliasTest() {
        SampleRef sampleRef = generateSampleRef(null, "testTeam", "testAlias");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        sampleRefValidator.validate(sampleRef, singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Pass, singleValidationResult.getValidationStatus());
    }

    @Test
    public void sampleUseNotFoundTest() {
        SampleUse sampleUse = generateSampleUse("SAMEA100001");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        sampleRefValidator.validateSampleUses(Arrays.asList(sampleUse), singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Error, singleValidationResult.getValidationStatus());
    }

    @Test
    public void sampleUseFoundTest() {
        SampleUse sampleUse = generateSampleUse("SAMEA123456");
        SingleValidationResult singleValidationResult = generateSingleValidationResult();

        sampleRefValidator.validateSampleUses(Arrays.asList(sampleUse), singleValidationResult);
        System.out.println(singleValidationResult.getMessage());

        Assert.assertEquals(ValidationStatus.Pass, singleValidationResult.getValidationStatus());
    }
}
