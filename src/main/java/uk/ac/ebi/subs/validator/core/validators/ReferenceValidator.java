package uk.ac.ebi.subs.validator.core.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.AbstractSubsRef;
import uk.ac.ebi.subs.data.component.AssayRef;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.component.SampleRelationship;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.component.StudyRef;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.repository.repos.submittables.AssayRepository;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;
import uk.ac.ebi.subs.repository.repos.submittables.StudyRepository;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.ValidationStatus;

import java.util.List;

@Service
@EnableMongoRepositories(basePackages = "uk.ac.ebi.subs.repository.repos.submittables")
public class ReferenceValidator {

    private final String FAIL_MESSAGE = "Could not find reference target: %s";
    private final String SUCCESS_MESSAGE = "Reference found.";

    @Autowired
    public StudyRepository studyRepository;
    @Autowired
    public AssayRepository assayRepository;
    @Autowired
    public SampleRepository sampleRepository;

    /**
     * An Assay refers to a Study via a StudyRef.
     * @param studyRef
     * @param singleValidationResult
     */
    public void validate(StudyRef studyRef, SingleValidationResult singleValidationResult) {
        Study study = studyRepository.findFirstByAccessionOrderByCreatedDateDesc(studyRef.getAccession());
        updateSingleValidationResult(study, studyRef, singleValidationResult);
    }

    /**
     * An AssayData refers to an Assay via an AssayRef
     * @param assayRef
     * @param singleValidationResult
     */
    public void validate(AssayRef assayRef, SingleValidationResult singleValidationResult) {
        Assay assay = assayRepository.findFirstByAccessionOrderByCreatedDateDesc(assayRef.getAccession());
        updateSingleValidationResult(assay, assayRef, singleValidationResult);
    }

    /**
     * An AssayData refers to a Sample via a SampleRef
     * @param sampleRef
     * @param singleValidationResult
     */
    public void validate(SampleRef sampleRef, SingleValidationResult singleValidationResult) {
        Sample sample = sampleRepository.findByAccession(sampleRef.getAccession());
        updateSingleValidationResult(sample, sampleRef, singleValidationResult);
    }

    /**
     * A Sample refers to other samples and itself via SampleRelationships
     * @param sampleRelationshipList
     * @param singleValidationResult
     * @return
     */
    public SingleValidationResult validateSampleRelationships(List<SampleRelationship> sampleRelationshipList, SingleValidationResult singleValidationResult) {
        StringBuilder accessions = new StringBuilder();

        for (SampleRelationship sampleRelationship : sampleRelationshipList) {
            Sample sample = sampleRepository.findByAccession(sampleRelationship.getAccession());

            if (sample == null) {
                if(accessions.toString().isEmpty()) {
                    accessions.append(sampleRelationship.getAccession());
                } else {
                    accessions.append(", " + sampleRelationship.getAccession());
                }
            }

            updateSingleValidationResult(accessions, singleValidationResult);
        }
        return singleValidationResult;
    }

    /**
     * An Assay refers to multiple Samples via SampleUses
     * @param sampleUseList
     * @param singleValidationResult
     */
    public void validateSampleUses(List<SampleUse> sampleUseList, SingleValidationResult singleValidationResult) {
        StringBuilder accessions = new StringBuilder();

        for (SampleUse sampleUse : sampleUseList) {
            Sample sample = sampleRepository.findByAccession(sampleUse.getSampleRef().getAccession());

            if (sample == null) {
                if(accessions.toString().isEmpty()) {
                    accessions.append(sampleUse.getSampleRef().getAccession());
                } else {
                    accessions.append(", " + sampleUse.getSampleRef().getAccession());
                }
            }

            updateSingleValidationResult(accessions, singleValidationResult);
        }
    }

    private void updateSingleValidationResult(Submittable submittable, AbstractSubsRef abstractSubsRef, SingleValidationResult singleValidationResult) {
        if(submittable == null) {
            singleValidationResult.setMessage(String.format(FAIL_MESSAGE, abstractSubsRef.getAccession()));
            singleValidationResult.setValidationStatus(ValidationStatus.Error);
        } else {
            singleValidationResult.setMessage(SUCCESS_MESSAGE);
            singleValidationResult.setValidationStatus(ValidationStatus.Pass);
        }
    }

    private void updateSingleValidationResult(StringBuilder accessions, SingleValidationResult singleValidationResult) {
        if (accessions.toString().isEmpty()) {
            singleValidationResult.setMessage(SUCCESS_MESSAGE);
            singleValidationResult.setValidationStatus(ValidationStatus.Pass);
        } else {
            singleValidationResult.setMessage(String.format(FAIL_MESSAGE, accessions));
            singleValidationResult.setValidationStatus(ValidationStatus.Error);
        }
    }
}
