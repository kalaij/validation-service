package uk.ac.ebi.subs.validator.core.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReferenceValidator.class)
public class ReferenceValidatorTest {

    @Autowired
    ReferenceValidator referenceValidator;

    Team team;
    static final String TEAM_NAME = "Test-Team";

    static final String EXPECTED_ID = "foo";

    @Before
    public void setup () {
        team = createTeam("TEAM_NAME");
    }

    @Test
    public void validateBySampleAcc() throws Exception {

        Submittable<Sample> sample = createSample(team);
        SampleRef sampleRef = new SampleRef();
        sampleRef.setAccession(sample.getAccession());

        SingleValidationResult result = referenceValidator.validate(EXPECTED_ID,sampleRef, sample);

        Assert.assertEquals(SingleValidationResultStatus.Pass, result.getValidationStatus());
        Assert.assertEquals(EXPECTED_ID, result.getEntityUuid());
        Assert.assertNull(result.getMessage());
    }

    @Test
    public void validateBySampleAlias() throws Exception {
        Submittable<Sample> sample = createSample(team);
        SampleRef sampleRef = new SampleRef();
        sampleRef.setAlias(sample.getAlias());
        sampleRef.setTeam(team.getName());

        SingleValidationResult result = referenceValidator.validate(EXPECTED_ID, sampleRef, sample);

        Assert.assertEquals(SingleValidationResultStatus.Pass, result.getValidationStatus());
        Assert.assertEquals(EXPECTED_ID, result.getEntityUuid());
        Assert.assertNull(result.getMessage());
    }

    @Test
    public void validateByNotSampleAcc() throws Exception {
        SampleRef sampleRef = new SampleRef();
        sampleRef.setAccession(UUID.randomUUID().toString());

        Submittable<Sample> nullSample = null;

        SingleValidationResult result = referenceValidator.validate(EXPECTED_ID, sampleRef, nullSample);

        Assert.assertEquals(SingleValidationResultStatus.Error, result.getValidationStatus());
    }

    @Test
    public void validateByNotSampleAlias() throws Exception {
        SampleRef sampleRef = new SampleRef();
        sampleRef.setAlias(UUID.randomUUID().toString());
        sampleRef.setTeam(team.getName());

        Submittable<Sample> nullSample = null;

        SingleValidationResult result = referenceValidator.validate(EXPECTED_ID, sampleRef, nullSample);

        Assert.assertEquals(SingleValidationResultStatus.Error, result.getValidationStatus());
    }

    @Test
    public void validateSampleRefNotinList() throws Exception {
        final List<Submittable<Sample>> sampleList = createSamples(team, 10);

        final List<SampleRef> sampleRefList = sampleList.stream().map(sample -> {
            SampleRef sampleRef = new SampleRef();
            sampleRef.setAccession(sample.getAccession());
            return sampleRef;
        }).collect(Collectors.toList());

        SampleRef sampleRefNotFound = new SampleRef();
        sampleRefNotFound.setAccession(UUID.randomUUID().toString());
        sampleRefList.add(sampleRefNotFound);


        List<SingleValidationResult> results = referenceValidator.validate(EXPECTED_ID, sampleRefList, sampleList);

        long errorCount = results.stream().filter(r -> r.getValidationStatus().equals(SingleValidationResultStatus.Error)).count();


        Assert.assertEquals(1L, errorCount);

    }

    static List<Submittable<Sample>> createSamples (Team team, int sampleNumber) {
        List<Submittable<Sample>> sampleList = new ArrayList<>(sampleNumber);
        for (int i = 0; i < sampleNumber; i++ ) {
            sampleList.add(createSample(team));
        }
        return sampleList;
    }

    static Submittable<Sample> createSample (Team team) {
        Sample sample = new Sample();
        sample.setTeam(team);
        String alias = UUID.randomUUID().toString();
        String accession = UUID.randomUUID().toString();
        sample.setAlias(alias);
        sample.setAccession(accession);

        return new Submittable<>(sample,"testSubId");

    }

    static Team createTeam (String teamName) {
        Team team = new Team();
        team.setName(teamName);
        return team;
    }

}