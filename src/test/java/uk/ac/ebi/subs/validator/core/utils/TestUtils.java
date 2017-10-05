package uk.ac.ebi.subs.validator.core.utils;

import uk.ac.ebi.subs.data.component.AssayRef;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.component.StudyRef;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestUtils {

    public static SingleValidationResult generateSingleValidationResult() {
        SingleValidationResult result = new SingleValidationResult(ValidationAuthor.Core, UUID.randomUUID().toString());
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

    public static StudyRef generateStudyRef(String accession) {
        return generateStudyRef(accession, null, null);
    }

    public static StudyRef generateStudyRef(String accession, String team, String alias) {
        StudyRef studyRef = new StudyRef();
        studyRef.setAccession(accession);
        studyRef.setTeam(team);
        studyRef.setAlias(alias);
        return studyRef;
    }
    public static AssayRef generateAssayRef(String accession) {
        return generateAssayRef(accession, null, null);
    }

    public static AssayRef generateAssayRef(String accession, String team, String alias) {
        AssayRef assayRef = new AssayRef();
        assayRef.setAccession(accession);
        assayRef.setTeam(team);
        assayRef.setAlias(alias);
        return assayRef;
    }

    public static List<Attribute> generateListOfAttributes() {
        List<Attribute> attributes = new ArrayList<>();

        Attribute att = new Attribute();
        att.setName("name");
        att.setValue("my name");
        attributes.add(att);

        Attribute att1 = new Attribute();
        att1.setName("name123456");
        att1.setValue("my name now");
        attributes.add(att1);

        return attributes;
    }
}
