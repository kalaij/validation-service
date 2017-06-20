package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.dto.AssayDto;
import uk.ac.ebi.subs.data.dto.SampleDto;
import uk.ac.ebi.subs.data.dto.StudyDto;
import uk.ac.ebi.subs.validator.data.SubmittableValidationEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

@Component
@ComponentScan("uk.ac.ebi.subs.messaging")
public class Coordinator {
    private static final Logger logger = LoggerFactory.getLogger(Coordinator.class);

    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    private ValidationResultRepository repository;

    @Autowired
    private ValidationResultService validationResultService;

    @Autowired
    public Coordinator(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    /**
     * Validator data entry point.
     * @param envelope
     */
    @RabbitListener(queues = Queues.SUBMISSION_SAMPLE_VALIDATOR)
    public void processSampleSubmission(SubmittableValidationEnvelope<SampleDto> envelope) {
        SampleDto sample = envelope.getEntityToValidate();

        if (sample == null) {
            throw new IllegalArgumentException("The envelop should contain a sample.");
        }

        logger.info("Received validation request on sample {}", sample.getId());
        handleSample(sample, envelope.getSubmissionId());
    }

    @RabbitListener(queues = Queues.SUBMISSION_STUDY_VALIDATOR)
    public void processStudySubmission(SubmittableValidationEnvelope<StudyDto> envelope){
        StudyDto study = envelope.getEntityToValidate();

        if (study == null) {
            throw new IllegalArgumentException("The envelop should contain a study.");
        }

        logger.info("Received validation request on study {}", study.getId());
        handleStudy(study, envelope.getSubmissionId());
    }

    @RabbitListener(queues = Queues.SUBMISSION_ASSAY_VALIDATOR)
    public void processAssaySubmission(SubmittableValidationEnvelope<AssayDto> envelope) {
        AssayDto assay = envelope.getEntityToValidate();

        if (assay == null) {
            throw new IllegalArgumentException("The envelop should contain an assay.");
        }

        logger.info("Received validation request on assay {}", assay.getId());
        handleAssay(assay, envelope.getSubmissionId());
    }

    private void handleSample(SampleDto sample, String submissionId) {
        ValidationResult validationResult = validationResultService.generateValidationResultDocument(sample, submissionId);
        repository.insert(validationResult);
        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        ValidationMessageEnvelope<SampleDto> messageEnvelope = new ValidationMessageEnvelope<>(validationResult.getUuid(), sample);

        logger.debug("Sending sample to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_CORE_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_TAXON_SAMPLE_CREATED, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_BIOSAMPLES_SAMPLE_CREATED, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_ENA_SAMPLE_CREATED, messageEnvelope);
    }

    private void handleStudy(StudyDto study, String submissionId) {
        ValidationResult validationResult = validationResultService.generateValidationResultDocument(study, submissionId);
        repository.insert(validationResult);
        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        ValidationMessageEnvelope<StudyDto> messageEnvelope = new ValidationMessageEnvelope<>(validationResult.getUuid(), study);

        logger.debug("Sending study to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_CORE_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_ENA_SAMPLE_CREATED, messageEnvelope);
    }

    private void handleAssay(AssayDto assay, String submissionId) {
        ValidationResult validationResult = validationResultService.generateValidationResultDocument(assay, submissionId);
        repository.insert(validationResult);
        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        ValidationMessageEnvelope<AssayDto> messageEnvelope = new ValidationMessageEnvelope<>(validationResult.getUuid(), assay);

        logger.debug("Sending assay to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_CORE_VALIDATION, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_ENA_SAMPLE_CREATED, messageEnvelope);
    }
}
