package uk.ac.ebi.subs.validator.core.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.TestAnnotationUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.core.utils.TestUtils;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static uk.ac.ebi.subs.validator.core.utils.TestUtils.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ReferenceValidator.class)
public class ReferenceValidatorTest {

    @Autowired
    ReferenceValidator referenceValidator;

    Team team;
    static final String TEAM_NAME = "Test-Team";
    SingleValidationResult singleValidationResult;

    @Before
    public void setup () {
        team = createTeam("TEAM_NAME");
        singleValidationResult = generateSingleValidationResult();
    }

    @Test
    public void validateBySampleAcc() throws Exception {
        Sample sample = createSample(team);
        SampleRef sampleRef = new SampleRef();
        sampleRef.setAccession(sample.getAccession());
        referenceValidator.validate(sample,sampleRef,singleValidationResult);
        Assert.assertEquals(SingleValidationResultStatus.Pass, singleValidationResult.getValidationStatus());
    }

    @Test
    public void validateBySampleAlias() throws Exception {
        Sample sample = createSample(team);
        SampleRef sampleRef = new SampleRef();
        sampleRef.setAlias(sample.getAlias());
        sampleRef.setTeam(team.getName());
        referenceValidator.validate(sample,sampleRef,singleValidationResult);
        Assert.assertEquals(SingleValidationResultStatus.Pass, singleValidationResult.getValidationStatus());
    }

    @Test
    public void validateByNotSampleAcc() throws Exception {
        SampleRef sampleRef = new SampleRef();
        sampleRef.setAccession(UUID.randomUUID().toString());
        referenceValidator.validate(null,sampleRef,singleValidationResult);
        Assert.assertEquals(SingleValidationResultStatus.Error, singleValidationResult.getValidationStatus());
    }

    @Test
    public void validateByNotSampleAlias() throws Exception {
        SampleRef sampleRef = new SampleRef();
        sampleRef.setAlias(UUID.randomUUID().toString());
        sampleRef.setTeam(team.getName());
        referenceValidator.validate(null,sampleRef,singleValidationResult);
        Assert.assertEquals(SingleValidationResultStatus.Error, singleValidationResult.getValidationStatus());
    }

    @Test
    public void validateSampleRefNotinList() throws Exception {
        final List<Sample> sampleList = createSamples(team, 10);

        final List<SampleRef> sampleRefList = sampleList.stream().map(sample -> {
            SampleRef sampleRef = new SampleRef();
            sampleRef.setAccession(sample.getAccession());
            return sampleRef;
        }).collect(Collectors.toList());

        SampleRef sampleRefNotFound = new SampleRef();
        sampleRefNotFound.setAccession(UUID.randomUUID().toString());
        sampleRefList.add(sampleRefNotFound);
        referenceValidator.validate(sampleList.toArray(
                new Sample[sampleList.size()]),
                sampleRefList.toArray(new SampleRef[sampleRefList.size()]),
                singleValidationResult
        );
        Assert.assertEquals(SingleValidationResultStatus.Error, singleValidationResult.getValidationStatus());

    }

    static List<Sample> createSamples (Team team, int sampleNumber) {
        List<Sample> sampleList = new ArrayList<>(sampleNumber);
        for (int i = 0; i < sampleNumber; i++ ) {
            sampleList.add(createSample(team));
        }
        return sampleList;
    }

    static Sample createSample (Team team) {
        Sample sample = new Sample();
        sample.setTeam(team);
        String alias = UUID.randomUUID().toString();
        String accession = UUID.randomUUID().toString();
        sample.setAlias(alias);
        sample.setAccession(accession);
        return  sample;
    }

    static Team createTeam (String teamName) {
        Team team = new Team();
        team.setName(teamName);
        return team;
    }

}