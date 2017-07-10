package uk.ac.ebi.subs.validator.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.AggregatorToFlipperEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;

/**
 * This class is listening on events on the validation result {@code Queue}.
 * When processing a published event it will update the {@code {@link uk.ac.ebi.subs.validator.data.ValidationResult}}
 * document with the validation results and publish a message of the updated document's UUID
 * to the validation result document update queue.
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
    public void handleValidationResult(SingleValidationResultsEnvelope singleValidationResultsEnvelope) {
        logger.debug("Received single validation results from {}.", singleValidationResultsEnvelope.getValidationAuthor());

        logger.debug("Trying to update Validation Result Document in MongoDB...");
        boolean success = validationResultService.updateValidationResult(singleValidationResultsEnvelope);

        if(success) {
            sendValidationResultDocumentUpdate(singleValidationResultsEnvelope);
        } else {
            logger.info("Ignoring obsolete validation results.");
        }
    }

    private void sendValidationResultDocumentUpdate(SingleValidationResultsEnvelope singleValidationResultsEnvelope) {
        logger.debug("Sending message: validation result document has been updated in MongoDB.");

        AggregatorToFlipperEnvelope envelope = new AggregatorToFlipperEnvelope(
                singleValidationResultsEnvelope.getValidationResultUUID(),
                singleValidationResultsEnvelope.getValidationResultVersion()
        );

        rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_VALIDATION_RESULT_DOCUMENT_UPDATED, envelope);
    }
}
