package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationOutcome;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;
import uk.ac.ebi.subs.validator.repository.ValidationOutcomeRepository;

import java.util.*;

@Component
public class Coordinator {
    private static final Logger logger = LoggerFactory.getLogger(Coordinator.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    private ValidationOutcomeRepository repository;

    @Autowired
    public Coordinator(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    @RabbitListener(queues = Queues.SUBMISSION_VALIDATOR)
    public void processSubmission(SubmissionEnvelope envelope) {
        logger.debug("Received validation request on submission {}", envelope.getSubmission().getId());

        // For now we are only prototyping with samples
        List<Sample> samples = envelope.getSamples();

        // TODO - Extract this as soon as we expand beyond just samples
        for (Sample sample : samples) {
            logger.debug("Validate the following object: {}", sample);

            // Generate and persist Validation Outcome Document
            ValidationOutcome validationOutcome = generateValidationOutcomeDocument(sample, envelope.getSubmission().getId());
            repository.insert(validationOutcome);
            logger.debug("Outcome document has been persisted into MongoDB");

            ValidationMessageEnvelope<Sample> messageEnvelope = new ValidationMessageEnvelope<>(
                    validationOutcome.getUuid(), sample);

            logger.debug("Send sample to validation queues");
            rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_BIOSAMPLES_SAMPLE_CREATED, messageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_ENA_SAMPLE_CREATED, messageEnvelope);
            rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_AE_SAMPLE_CREATED, messageEnvelope);
        }

    }

    private ValidationOutcome generateValidationOutcomeDocument(Sample sample, String submissionId) {
        logger.debug("Create Outcome Document");

        ValidationOutcome outcomeDocument = new ValidationOutcome();
        outcomeDocument.setUuid(UUID.randomUUID().toString());
        outcomeDocument.setSubmissionId(submissionId);

        outcomeDocument.setEntityUuid(sample.getId());

        Map<Archive, Boolean> expectedOutcomes = new HashMap<>();
        for (Archive archive : Arrays.asList(Archive.BioSamples, Archive.Ena, Archive.ArrayExpress)) {
            expectedOutcomes.put(archive, false);
        }
        outcomeDocument.setExpectedOutcomes(expectedOutcomes);

        return outcomeDocument;
    }
}
