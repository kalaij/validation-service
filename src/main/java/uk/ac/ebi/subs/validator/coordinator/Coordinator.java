package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.validator.data.*;

import static uk.ac.ebi.subs.validator.messaging.CoordinatorQueues.*;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorRoutingKeys.*;

@Component
public class Coordinator {
    private static final Logger logger = LoggerFactory.getLogger(Coordinator.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    private ValidationResultService validationResultService;

    @Autowired
    public Coordinator(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    /**
     * Sample validator data entry point.
     * @param envelope contains the {@link Sample} entity to validate
     * @return true if it could create a {@link ValidationMessageEnvelope} containing the {@link Sample} entity and
     * the UUID of the {@link ValidationResult}
     */
    @RabbitListener(queues = SUBMISSION_SAMPLE_VALIDATOR)
    public void processSampleSubmission(SubmittedSampleValidationEnvelope envelope) {
        Sample sample = envelope.getEntityToValidate();

        if (sample == null) {
            throw new IllegalArgumentException("The envelop should contain a sample.");
        }

        logger.info("Received validation request on sample with id {}", sample.getId());

        if (!handleSample(sample)) {
            logger.error("Error handling sample with id {}", sample.getId());
        }
    }

    private boolean handleSample(Sample sample) {
        ValidationResult validationResult = validationResultService.generateValidationResultDocument(sample);

        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        ValidationMessageEnvelope<Sample> messageEnvelope = new ValidationMessageEnvelope<>(validationResult.getUuid(), validationResult.getVersion(), sample);

        logger.debug("Sending sample to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_SAMPLE_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_SAMPLE_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_TAXON_SAMPLE_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_BIOSAMPLES_SAMPLE_VALIDATION, messageEnvelope);

        return validationResult.getEntityUuid() != null;
    }

    /**
     * Study validator data entry point.
     * @param envelope contains the {@link Study} entity to validate
     * @return true if it could create a {@link ValidationMessageEnvelope} containing the {@link Study} entity and
     * the UUID of the {@link ValidationResult}
     */
    @RabbitListener(queues = SUBMISSION_STUDY_VALIDATOR)
    public void processStudySubmission(SubmittedStudyValidationEnvelope envelope){
        Study study = envelope.getEntityToValidate();

        if (study == null) {
            throw new IllegalArgumentException("The envelop should contain a study.");
        }

        logger.info("Received validation request on study with id {}", study.getId());

        if (!handleStudy(study)) {
            logger.error("Error handling study with id {}", study.getId());
        }
    }

    private boolean handleStudy(Study study) {
        ValidationResult validationResult = validationResultService.generateValidationResultDocument(study);

        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        ValidationMessageEnvelope<Study> messageEnvelope = new ValidationMessageEnvelope<>(validationResult.getUuid(), validationResult.getVersion(), study);

        logger.debug("Sending study to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_STUDY_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_STUDY_VALIDATION, messageEnvelope);

        return validationResult.getEntityUuid() != null;
    }

    /**
     * Assay validator data entry point.
     * @param envelope contains the {@link Assay} entity to validate
     * @return true if it could create a {@link ValidationMessageEnvelope} with the {@link Assay} entity and
     * the UUID of the {@link ValidationResult}
     */
    @RabbitListener(queues = SUBMISSION_ASSAY_VALIDATOR)
    public void processAssaySubmission(SubmittedAssayValidationEnvelope envelope) {
        Assay assay = envelope.getEntityToValidate();

        if (assay == null) {
            throw new IllegalArgumentException("The envelop should contain an assay.");
        }

        logger.info("Received validation request on assay {}", assay.getId());

        if (!handleAssay(assay)) {
            logger.error("Error handling assay with id {}", assay.getId());
        }
    }

    private boolean handleAssay(Assay assay) {
        ValidationResult validationResult = validationResultService.generateValidationResultDocument(assay);

        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        ValidationMessageEnvelope<Assay> messageEnvelope = new ValidationMessageEnvelope<>(validationResult.getUuid(), validationResult.getVersion(), assay);

        logger.debug("Sending assay to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_ASSAY_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_ASSAY_VALIDATION, messageEnvelope);

        return validationResult.getEntityUuid() != null;
    }

    /**
     * AssayData validator data entry point.
     * @param envelope contains the {@link AssayData} entity to validate
     * @return true if it could create a {@link ValidationMessageEnvelope} with the {@link AssayData} entity and
     * the UUID of the {@link ValidationResult}
     */
    @RabbitListener(queues = SUBMISSION_ASSAY_DATA_VALIDATOR)
    public void processAssayDataSubmission(SubmittedAssayDataValidationEnvelope envelope) {
        AssayData assayData = envelope.getEntityToValidate();

        if (assayData == null) {
            throw new IllegalArgumentException("The envelop should contain an assay data.");
        }

        logger.info("Received validation request on assay data {}", assayData.getId());

        if (!handleAssayData(assayData)) {
            logger.error("Error handling assayData with id {}", assayData.getId());
        }
    }

    private boolean handleAssayData(AssayData assayData) {
        ValidationResult validationResult = validationResultService.generateValidationResultDocument(assayData);

        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        ValidationMessageEnvelope<AssayData> messageEnvelope = new ValidationMessageEnvelope<>(validationResult.getUuid(), validationResult.getVersion(), assayData);

        logger.debug("Sending assay data to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_CORE_ASSAYDATA_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_ENA_ASSAYDATA_VALIDATION, messageEnvelope);

        return validationResult.getEntityUuid() != null;
    }
}
