package uk.ac.ebi.subs.validator.coordinator;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.AssayRef;
import uk.ac.ebi.subs.data.component.SampleRef;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.repository.repos.submittables.AssayRepository;
import uk.ac.ebi.subs.repository.repos.submittables.SampleRepository;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.model.Submittable;

@Service
public class AssayDataValidationMessageEnvelopeExpander extends ValidationMessageEnvelopeExpander<AssayDataValidationMessageEnvelope> {

    AssayRepository assayRepository;
    SampleRepository sampleRepository;

    public AssayDataValidationMessageEnvelopeExpander(AssayRepository assayRepository, SampleRepository sampleRepository) {
        this.assayRepository = assayRepository;
        this.sampleRepository = sampleRepository;
    }

    @Override
    public void expandEnvelope(AssayDataValidationMessageEnvelope assayDataValidationMessageEnvelope, String submissionId) {
        final AssayRef assayRef = assayDataValidationMessageEnvelope.getEntityToValidate().getAssayRef();

        uk.ac.ebi.subs.repository.model.Assay assayStoredSubmittable;

        if (assayRef.getAccession() != null && !assayRef.getAccession().isEmpty()) {
            assayStoredSubmittable = assayRepository.findFirstByAccessionOrderByCreatedDateDesc(assayRef.getAccession());
        } else {
            assayStoredSubmittable = assayRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc(assayRef.getTeam(), assayRef.getAlias());
        }

        Submittable<Assay> assaySubmittable = new Submittable<>(assayStoredSubmittable,submissionId);
        assayDataValidationMessageEnvelope.setAssay(assaySubmittable);

        final SampleRef sampleRef = assayDataValidationMessageEnvelope.getEntityToValidate().getSampleRef();

        uk.ac.ebi.subs.repository.model.Sample sampleStoredSubmittable;

        if (sampleRef.getAccession() != null && !sampleRef.getAccession().isEmpty()) {
            sampleStoredSubmittable = sampleRepository.findFirstByAccessionOrderByCreatedDateDesc(sampleRef.getAccession());
        } else {
            sampleStoredSubmittable = sampleRepository.findFirstByTeamNameAndAliasOrderByCreatedDateDesc(sampleRef.getTeam(), sampleRef.getAlias());
        }

        Submittable<Sample> sampleSubmittable = new Submittable<>(sampleStoredSubmittable,submissionId);
        assayDataValidationMessageEnvelope.setSample(sampleSubmittable);

    }


}
