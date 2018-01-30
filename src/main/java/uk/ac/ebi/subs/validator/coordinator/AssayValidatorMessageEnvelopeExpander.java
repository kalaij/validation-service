package uk.ac.ebi.subs.validator.coordinator;

import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.data.component.StudyRef;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.repository.model.Sample;
import uk.ac.ebi.subs.repository.model.Study;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;
import uk.ac.ebi.subs.repository.repos.submittables.StudyRepository;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;

import java.util.List;

public class AssayValidatorMessageEnvelopeExpander extends ValidationMessageEnvelopeExpander<AssayValidationMessageEnvelope> {

    SampleRepository sampleRepository;
    StudyRepository studyRepository;

    public AssayValidatorMessageEnvelopeExpander(SampleRepository sampleRepository, StudyRepository studyRepository) {
        this.sampleRepository = sampleRepository;
        this.studyRepository = studyRepository;
    }

    @Override
    public void expandEnvelope(AssayValidationMessageEnvelope validationMessageEnvelope, String submissionId) {
        final Assay entityToValidate = validationMessageEnvelope.getEntityToValidate();

        final List<SampleUse> sampleUses = entityToValidate.getSampleUses();

        for (SampleUse sampleUse : sampleUses) {

            Sample sample;

            if (sampleUse.getSampleRef().getAccession() != null && !sampleUse.getSampleRef().getAccession().isEmpty()) {
                sample = sampleRepository.findByAccession(sampleUse.getSampleRef().getAccession());
            } else {
                sample = sampleRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc(sampleUse.getSampleRef().getTeam(), sampleUse.getSampleRef().getAlias());
            }

            if (sample.getSubmission().getId().equals(submissionId)) {
                validationMessageEnvelope.getSampleList().add(sample);
            }
        }

        final StudyRef studyRef = entityToValidate.getStudyRef();

        Study study;

        if (studyRef.getAccession() != null && !studyRef.getAccession().isEmpty()) {
            study = studyRepository.findFirstByAccessionOrderByCreatedDateDesc(studyRef.getAccession());
        } else {
            study = studyRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc(studyRef.getTeam(), studyRef.getAlias());
        }

       if (addToValidationEnvelope(study,submissionId)) {
           validationMessageEnvelope.setStudy(study);
       }

    }
}
