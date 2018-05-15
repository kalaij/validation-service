package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationResult;

import java.util.Optional;

import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_BIOSAMPLES_SAMPLE_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_BIOSTUDIES_PROJECT_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_CORE_ASSAYDATA_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_CORE_ASSAY_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_CORE_SAMPLE_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_CORE_STUDY_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_ENA_ASSAYDATA_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_ENA_ASSAY_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_ENA_SAMPLE_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_ENA_STUDY_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_SCHEMA_ASSAYDATA_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_SCHEMA_ASSAY_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_SCHEMA_SAMPLE_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_SCHEMA_STUDY_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_TAXON_SAMPLE_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.FileReferenceRoutingKeys.EVENT_ASSAYDATA_FILEREF_VALIDATION;

@Component
public class SubmittableHandler {
    private static final Logger logger = LoggerFactory.getLogger(SubmittableHandler.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    private CoordinatorValidationResultService coordinatorValidationResultService;
    private SampleValidationMessageEnvelopeExpander sampleValidationMessageEnvelopeExpander;
    private StudyValidationMessageEnvelopeExpander studyValidationMessageEnvelopeExpander;
    private AssayValidationMessageEnvelopeExpander assayValidationMessageEnvelopeExpander;
    private AssayDataValidationMessageEnvelopeExpander assayDataValidationMessageEnvelopeExpander;

    public SubmittableHandler(
            RabbitMessagingTemplate rabbitMessagingTemplate,
            CoordinatorValidationResultService coordinatorValidationResultService,
            SampleValidationMessageEnvelopeExpander sampleValidationMessageEnvelopeExpander,
            StudyValidationMessageEnvelopeExpander studyValidationMessageEnvelopeExpander,
            AssayValidationMessageEnvelopeExpander assayValidationMessageEnvelopeExpander,
            AssayDataValidationMessageEnvelopeExpander assayDataValidationMessageEnvelopeExpander) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.coordinatorValidationResultService = coordinatorValidationResultService;
        this.sampleValidationMessageEnvelopeExpander = sampleValidationMessageEnvelopeExpander;
        this.studyValidationMessageEnvelopeExpander = studyValidationMessageEnvelopeExpander;
        this.assayValidationMessageEnvelopeExpander = assayValidationMessageEnvelopeExpander;
        this.assayDataValidationMessageEnvelopeExpander = assayDataValidationMessageEnvelopeExpander;
    }

    /**
     * @param project
     * @return true if it could create a {@link ValidationMessageEnvelope} with the {@link Project} entity and
     * the UUID of the {@link ValidationResult}
     */
    protected boolean handleSubmittable(Project project) {
        Optional<ValidationResult> optionalValidationResult = coordinatorValidationResultService.fetchValidationResultDocument(project);
        if (optionalValidationResult.isPresent()) {
            ValidationResult validationResult = optionalValidationResult.get();
            logger.trace("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

            ValidationMessageEnvelope<Project> messageEnvelope = new ValidationMessageEnvelope<>(validationResult.getUuid(), validationResult.getVersion(), project);

            logger.trace("Sending project to validation queue");
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_BIOSTUDIES_PROJECT_VALIDATION, messageEnvelope);

            return validationResult.getEntityUuid() != null;
        }
        return false;
    }

    /**
     * @param sample
     * @param submissionId
     * @return true if it could create a {@link ValidationMessageEnvelope} containing the {@link Sample} entity and
     * the UUID of the {@link ValidationResult}
     */
    protected boolean handleSubmittable(Sample sample, String submissionId) {
        Optional<ValidationResult> optionalValidationResult = coordinatorValidationResultService.fetchValidationResultDocument(sample);
        if (optionalValidationResult.isPresent()) {
            ValidationResult validationResult = optionalValidationResult.get();
            logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

            SampleValidationMessageEnvelope messageEnvelope = new SampleValidationMessageEnvelope(validationResult.getUuid(), validationResult.getVersion(), sample, submissionId);
            sampleValidationMessageEnvelopeExpander.expandEnvelope(messageEnvelope);

            logger.debug("Sending sample to validation queues");
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_SAMPLE_VALIDATION, messageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_SAMPLE_VALIDATION, messageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_TAXON_SAMPLE_VALIDATION, messageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_BIOSAMPLES_SAMPLE_VALIDATION, messageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_SCHEMA_SAMPLE_VALIDATION, messageEnvelope);

            return validationResult.getEntityUuid() != null;
        }
        return false;
    }

    /**
     * @param study
     * @param submissionId
     * @return true if it could create a {@link ValidationMessageEnvelope} containing the {@link Study} entity and
     * the UUID of the {@link ValidationResult}
     */
    protected boolean handleSubmittable(Study study, String submissionId) {
        Optional<ValidationResult> optionalValidationResult = coordinatorValidationResultService.fetchValidationResultDocument(study);
        if (optionalValidationResult.isPresent()) {
            ValidationResult validationResult = optionalValidationResult.get();
            logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

            StudyValidationMessageEnvelope studyValidationMessageEnvelope = new StudyValidationMessageEnvelope(validationResult.getUuid(), validationResult.getVersion(), study,submissionId);
            studyValidationMessageEnvelopeExpander.expandEnvelope(studyValidationMessageEnvelope);

            logger.debug("Sending study to validation queues");
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_STUDY_VALIDATION, studyValidationMessageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_STUDY_VALIDATION, studyValidationMessageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_SCHEMA_STUDY_VALIDATION, studyValidationMessageEnvelope);

            return validationResult.getEntityUuid() != null;
        }
        return false;
    }

    /**
     * @param assay
     * @param submissionId
     * @return true if it could create a {@link ValidationMessageEnvelope} with the {@link Assay} entity and
     * the UUID of the {@link ValidationResult}
     */
    protected boolean handleSubmittable(Assay assay, String submissionId) {
        Optional<ValidationResult> optionalValidationResult = coordinatorValidationResultService.fetchValidationResultDocument(assay);
        if (optionalValidationResult.isPresent()) {
            ValidationResult validationResult = optionalValidationResult.get();
            logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());
            AssayValidationMessageEnvelope assayValidationMessageEnvelope = new AssayValidationMessageEnvelope(validationResult.getUuid(), validationResult.getVersion(), assay,submissionId);
            assayValidationMessageEnvelopeExpander.expandEnvelope(assayValidationMessageEnvelope);

            logger.debug("Sending assay to validation queues");
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_ASSAY_VALIDATION, assayValidationMessageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_ASSAY_VALIDATION, assayValidationMessageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_SCHEMA_ASSAY_VALIDATION, assayValidationMessageEnvelope);

            return validationResult.getEntityUuid() != null;
        }
        return false;
    }

    /**
     * @param assayData
     * @param submissionId
     * @return true if it could create a {@link ValidationMessageEnvelope} with the {@link AssayData} entity and
     * the UUID of the {@link ValidationResult}
     */
    protected boolean handleSubmittable(AssayData assayData, String submissionId) {
        Optional<ValidationResult> optionalValidationResult = coordinatorValidationResultService.fetchValidationResultDocument(assayData);
        if (optionalValidationResult.isPresent()) {
            ValidationResult validationResult = optionalValidationResult.get();
            logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

            AssayDataValidationMessageEnvelope assayDataValidationMessageEnvelope = new AssayDataValidationMessageEnvelope(validationResult.getUuid(), validationResult.getVersion(), assayData,submissionId);
            assayDataValidationMessageEnvelopeExpander.expandEnvelope(assayDataValidationMessageEnvelope);

            logger.debug("Sending assay data to validation queues");
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_ASSAYDATA_VALIDATION, assayDataValidationMessageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_ASSAYDATA_VALIDATION, assayDataValidationMessageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_SCHEMA_ASSAYDATA_VALIDATION, assayDataValidationMessageEnvelope);

            return validationResult.getEntityUuid() != null;
        }
        return false;
    }

    protected boolean handleSubmittableForFileOperation(AssayData assayData, String submissionId) {
        Optional<ValidationResult> optionalValidationResult = coordinatorValidationResultService.fetchValidationResultDocument(assayData);
        if (optionalValidationResult.isPresent()) {
            ValidationResult validationResult = optionalValidationResult.get();
            logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

            AssayDataValidationMessageEnvelope assayDataValidationMessageEnvelope = new AssayDataValidationMessageEnvelope(validationResult.getUuid(), validationResult.getVersion(), assayData,submissionId);
            assayDataValidationMessageEnvelopeExpander.expandEnvelope(assayDataValidationMessageEnvelope);

            logger.debug("Sending assay data to file reference validation queue");
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ASSAYDATA_FILEREF_VALIDATION, assayDataValidationMessageEnvelope);

            logger.debug("Sending assay data to validation queue");
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_ASSAYDATA_VALIDATION, assayDataValidationMessageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_ASSAYDATA_VALIDATION, assayDataValidationMessageEnvelope);

            return validationResult.getEntityUuid() != null;
        }
        return false;
    }

    protected void handleSubmittable(Submittable submittable, String submissionId) {
        if(submittable instanceof Project) {
            handleSubmittable((Project) submittable);
        } else if(submittable instanceof Sample) {
            handleSubmittable((Sample) submittable, submissionId);
        } else if(submittable instanceof Study) {
            handleSubmittable((Study) submittable, submissionId);
        } else if(submittable instanceof Assay) {
            handleSubmittable((Assay) submittable, submissionId);
        } else if(submittable instanceof AssayData) {
            handleSubmittable((AssayData) submittable, submissionId);
        } else {
            logger.error("Could not understand submittable {}", submittable);
        }
    }
}
