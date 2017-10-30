package uk.ac.ebi.subs.validator.flipper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.AggregatorToFlipperEnvelope;

import static uk.ac.ebi.subs.validator.messaging.StatusFlipperQueues.VALIDATION_RESULT_DOCUMENT_UPDATE;

/**
 * This class is listening on events on the validation aggregation results {@code Queue}.
 * When processing a published event it will update the {@code ValidationResult} document's status
 * according to the availability of the validation results. If all the entity has been validated,
 * then the status will change to {@code Complete}, otherwise it will stay {@code Pending} as initially.
 */
@Service
public class StatusFlipperListener {
    public static final Logger logger = LoggerFactory.getLogger(StatusFlipperListener.class);

    private StatusFlipperValidationResultService statusFlipperValidationResultService;

    @Autowired
    public StatusFlipperListener(StatusFlipperValidationResultService statusFlipperValidationResultService ) {
        this.statusFlipperValidationResultService = statusFlipperValidationResultService;
    }

    @RabbitListener(queues = VALIDATION_RESULT_DOCUMENT_UPDATE)
    public void processUpdate(AggregatorToFlipperEnvelope envelope) {
        logger.debug("Processing validation result document update with id {}.", envelope.getValidationResultUuid());

        if (!statusFlipperValidationResultService.updateValidationResult(envelope)) {
            logger.debug("Ignoring obsolete validation documents.");
        }
    }
}