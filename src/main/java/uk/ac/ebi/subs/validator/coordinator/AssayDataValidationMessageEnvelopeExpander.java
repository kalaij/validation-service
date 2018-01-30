package uk.ac.ebi.subs.validator.coordinator;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.AssayRef;
import uk.ac.ebi.subs.repository.repos.submittables.AssayRepository;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;

@Service
public class AssayDataValidationMessageEnvelopeExpander extends ValidationMessageEnvelopeExpander<AssayDataValidationMessageEnvelope> {

    AssayRepository assayRepository;

    public AssayDataValidationMessageEnvelopeExpander(AssayRepository assayRepository) {
        this.assayRepository = assayRepository;
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

        if (addToValidationEnvelope(assayStoredSubmittable,submissionId)) {
            assayDataValidationMessageEnvelope.setAssay(assayStoredSubmittable);
        }

    }


}
