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
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.repository.model.StoredSubmittable;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationResult;

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
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.EVENT_TAXON_SAMPLE_VALIDATION;

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
        ValidationResult validationResult = coordinatorValidationResultService.generateValidationResultDocument(project);

        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        ValidationMessageEnvelope<Project> messageEnvelope = new ValidationMessageEnvelope<>(validationResult.getUuid(), validationResult.getVersion(), project);

        logger.debug("Sending project to validation queue");
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_BIOSTUDIES_PROJECT_VALIDATION, messageEnvelope);

        return validationResult.getEntityUuid() != null;
    }

    /**
     * @param sample
     * @param submissionId
     * @return true if it could create a {@link ValidationMessageEnvelope} containing the {@link Sample} entity and
     * the UUID of the {@link ValidationResult}
     */
    protected boolean handleSubmittable(Sample sample, String submissionId) {
        ValidationResult validationResult = coordinatorValidationResultService.generateValidationResultDocument(sample);

        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        SampleValidationMessageEnvelope messageEnvelope = new SampleValidationMessageEnvelope(validationResult.getUuid(), validationResult.getVersion(), sample, submissionId);
        sampleValidationMessageEnvelopeExpander.expandEnvelope(messageEnvelope);

        logger.debug("Sending sample to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_SAMPLE_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_SAMPLE_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_TAXON_SAMPLE_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_BIOSAMPLES_SAMPLE_VALIDATION, messageEnvelope);

        return validationResult.getEntityUuid() != null;
    }

    /**
     * @param study
     * @param submissionId
     * @return true if it could create a {@link ValidationMessageEnvelope} containing the {@link Study} entity and
     * the UUID of the {@link ValidationResult}
     */
    protected boolean handleSubmittable(Study study, String submissionId) {
        ValidationResult validationResult = coordinatorValidationResultService.generateValidationResultDocument(study);

        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        StudyValidationMessageEnvelope studyValidationMessageEnvelope = new StudyValidationMessageEnvelope(validationResult.getUuid(), validationResult.getVersion(), study,submissionId);
        studyValidationMessageEnvelopeExpander.expandEnvelope(studyValidationMessageEnvelope);

        logger.debug("Sending study to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_STUDY_VALIDATION, studyValidationMessageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_STUDY_VALIDATION, studyValidationMessageEnvelope);

        return validationResult.getEntityUuid() != null;
    }

    /**
     * @param assay
     * @param submissionId
     * @return true if it could create a {@link ValidationMessageEnvelope} with the {@link Assay} entity and
     * the UUID of the {@link ValidationResult}
     */
    protected boolean handleSubmittable(Assay assay, String submissionId) {
        ValidationResult validationResult = coordinatorValidationResultService.generateValidationResultDocument(assay);

        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());
        AssayValidationMessageEnvelope assayValidationMessageEnvelope = new AssayValidationMessageEnvelope(validationResult.getUuid(), validationResult.getVersion(), assay,submissionId);
        assayValidationMessageEnvelopeExpander.expandEnvelope(assayValidationMessageEnvelope);

        logger.debug("Sending assay to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_ASSAY_VALIDATION, assayValidationMessageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_ASSAY_VALIDATION, assayValidationMessageEnvelope);

        return validationResult.getEntityUuid() != null;
    }

    /**
     * @param assayData
     * @param submissionId
     * @return true if it could create a {@link ValidationMessageEnvelope} with the {@link AssayData} entity and
     * the UUID of the {@link ValidationResult}
     */
    protected boolean handleSubmittable(AssayData assayData, String submissionId) {
        ValidationResult validationResult = coordinatorValidationResultService.generateValidationResultDocument(assayData);

        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        AssayDataValidationMessageEnvelope assayDataValidationMessageEnvelope = new AssayDataValidationMessageEnvelope(validationResult.getUuid(), validationResult.getVersion(), assayData,submissionId);
        assayDataValidationMessageEnvelopeExpander.expandEnvelope(assayDataValidationMessageEnvelope);

        logger.debug("Sending assay data to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_ASSAYDATA_VALIDATION, assayDataValidationMessageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_ASSAYDATA_VALIDATION, assayDataValidationMessageEnvelope);

        return validationResult.getEntityUuid() != null;
    }

    protected boolean handleStoredSubmittable(StoredSubmittable storedSubmittable) {
        // TODO some magic here
        return false;
    }
}
