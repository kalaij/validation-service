package uk.ac.ebi.subs.validator.coordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationOutcome;
import uk.ac.ebi.subs.validator.data.ValidationOutcomeEnum;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;
import uk.ac.ebi.subs.validator.repository.repository.ValidationOutcomeRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Coordinator {
    private static final Logger logger = LoggerFactory.getLogger(Coordinator.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    private ValidationOutcomeRepository repository;

    @Autowired
    public Coordinator(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
    }

    private void processSubmission(SubmissionEnvelope envelope) {
        logger.debug("Received validation request on submission {}", envelope.getSubmission().getId());

        // For now we are only prototyping with samples
        List<Sample> samples = envelope.getSamples();

        // TODO - Extract this as soon as we expand beyond just samples to use EntityProcessor
        if (samples.size() > 0) {
            for (Sample sample : samples) {

                // Generate and persist Validation Outcome Document
                ValidationOutcome validationOutcome = generateValidationOutcomeDocument(sample);
                repository.insert(validationOutcome);

                // Send sample to validation queues
                rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_BIOSAMPLES_SAMPLE_CREATED, sample);
                rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_ENA_SAMPLE_CREATED, sample);
                rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_AE_SAMPLE_CREATED, sample);
            }
        }
    }

    private ValidationOutcome generateValidationOutcomeDocument(Sample sample) {
        ValidationOutcome validationOutcome = new ValidationOutcome();
        validationOutcome.setEntityUuid(sample.getId());
        validationOutcome.setValidationOutcome(ValidationOutcomeEnum.Pending);

        List<Archive> archiveList = Arrays.asList(Archive.BioSamples, Archive.Ena, Archive.ArrayExpress);

        Map<Archive, Boolean> expectedOutcomes = new HashMap<>();
        for (Archive archive : archiveList) {
            expectedOutcomes.put(archive, false);
        }
        validationOutcome.setExpectedOutcomes(expectedOutcomes);
        return validationOutcome;
    }
}
