package uk.ac.ebi.subs.validator.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.EntityValidationOutcome;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;

/**
 * This class is listening on events on the validation result {@code Queue}.
 * When processing a published event it will update the {@code ValidationOutcome} document with the validation results
 * and publish a message of the updated document's UUID to the outcome document update queue.
 *
 * Created by karoly on 05/05/2017.
 */
@Service
public class ValidationAggregatorListener {

    private static Logger logger = LoggerFactory.getLogger(ValidationAggregatorListener.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    ValidationAggregator validationAggregator;

    @Autowired
    public ValidationAggregatorListener(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    @RabbitListener(queues = Queues.VALIDATION_RESULT)
    public void handleValidationResult(EntityValidationOutcome validationOutcome) {
        logger.debug("Received validation outcome.");

        validationAggregator.updateValidationOutcome(validationOutcome);

        logger.debug("Validation outcome has been updated in MongoDB.");

        sendOutcomeDocumentUpdate(validationOutcome);
    }

    private void sendOutcomeDocumentUpdate(EntityValidationOutcome validationOutcome) {
        logger.debug("Sending message: outcome document has been updated in MongoDB.");

        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_OUTCOME_DOCUMENT_UPDATED,
                validationOutcome.getOutcomeDocumentUUID());
    }
}
