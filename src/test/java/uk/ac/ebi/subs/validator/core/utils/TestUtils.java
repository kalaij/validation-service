package uk.ac.ebi.subs.validator.core.utils;

import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationAuthor;

import java.util.UUID;

public class TestUtils {

    public static SingleValidationResult generateSingleValidationResult() {
        SingleValidationResult result = new SingleValidationResult(ValidationAuthor.Core, UUID.randomUUID().toString());
        result.setUuid(UUID.randomUUID().toString());
        return result;
    }

    public static SampleRelationship generateSampleRelationship(String accession) {
        SampleRelationship relationship = new SampleRelationship();
        relationship.setAccession(accession);
        return relationship;
    }

    public static SampleRef generateSampleRef(String accession) {
        return generateSampleRef(accession, null, null);
    }

    public static SampleRef generateSampleRef(String accession, String team, String alias) {
        SampleRef sampleRef = new SampleRef();
        sampleRef.setAccession(accession);
        sampleRef.setTeam(team);
        sampleRef.setAlias(alias);
        return sampleRef;
    }

    public static SampleUse generateSampleUse(String accession) {
        SampleUse sampleUse = new SampleUse();
        sampleUse.setSampleRef(generateSampleRef(accession));
        return sampleUse;
    }
}
