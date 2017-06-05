package uk.ac.ebi.subs.validator.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;

/**
 * This class is listening on events on the validation result {@code Queue}.
 * When processing a published event it will update the {@code {@link uk.ac.ebi.subs.validator.data.ValidationResult}}
 * document with the validation results and publish a message of the updated document's UUID
 * to the outcome document update queue.
 *
 * Created by karoly on 05/05/2017.
 */
@Service
public class AggregatorListener {

    private static Logger logger = LoggerFactory.getLogger(AggregatorListener.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    ValidationResultService validationResultService;

    @Autowired
    public AggregatorListener(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    @RabbitListener(queues = Queues.VALIDATION_RESULT)
    public void handleValidationResult(SingleValidationResult singleValidationResult) {
        logger.debug("Received single validation result for an entity.");

        logger.debug("Trying to update Validation Outcome Document in MongoDB...");
        boolean success = validationResultService.updateValidationResult(singleValidationResult);

        if(success) {
            sendOutcomeDocumentUpdate(singleValidationResult);
        } else {
            logger.info("Ignoring obsolete validation results.");
        }
    }

    private void sendOutcomeDocumentUpdate(SingleValidationResult validationOutcome) {
        logger.debug("Sending message: outcome document has been updated in MongoDB.");

        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_VALIDATION_RESULT_DOCUMENT_UPDATED,
                validationOutcome.getValidationResultUUID());
    }
}
