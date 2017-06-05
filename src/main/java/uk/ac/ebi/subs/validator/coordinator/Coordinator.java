package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.validator.data.SubmittableValidationEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationResult;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;
import uk.ac.ebi.subs.validator.repository.ValidationResultRepository;

@Component
public class Coordinator {
    private static final Logger logger = LoggerFactory.getLogger(Coordinator.class);

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
    public void processSampleSubmission(SubmittableValidationEnvelope<Sample> envelope) {
        Sample sample = envelope.getEntityToValidate();

        if (sample == null) {
            throw new IllegalArgumentException("The envelop should contain a sample.");
        }

        logger.info("Received validation request on sample {}", sample.getId());
        handleSample(sample, envelope.getSubmissionId());
    }

    private void handleSample(Sample sample, String submissionId) {
        ValidationResult validationResult = validationResultService.generateValidationResultDocument(sample, submissionId);
        repository.insert(validationResult);
        logger.debug("Validation result document has been persisted into MongoDB with ID: {}", validationResult.getUuid());

        ValidationMessageEnvelope<Sample> messageEnvelope = new ValidationMessageEnvelope<>(validationResult.getUuid(), sample);

        logger.debug("Sending sample to validation queues");
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_BIOSAMPLES_SAMPLE_CREATED, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_ENA_SAMPLE_CREATED, messageEnvelope);
        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_AE_SAMPLE_CREATED, messageEnvelope);
    }

}
