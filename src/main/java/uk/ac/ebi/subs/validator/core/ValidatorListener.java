package uk.ac.ebi.subs.validator.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.core.handlers.AssayDataHandler;
import uk.ac.ebi.subs.validator.core.handlers.AssayHandler;
import uk.ac.ebi.subs.validator.core.handlers.SampleHandler;
import uk.ac.ebi.subs.validator.core.handlers.StudyHandler;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationStatus;
import uk.ac.ebi.subs.validator.messaging.Exchanges;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValidatorListener {
    private static Logger logger = LoggerFactory.getLogger(ValidatorListener.class);

    @Autowired
    AssayHandler assayHandler;
    @Autowired
    AssayDataHandler assayDataHandler;
    @Autowired
    SampleHandler sampleHandler;
    @Autowired
    StudyHandler studyHandler;

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    public ValidatorListener(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    @RabbitListener(queues = Queues.CORE_ASSAY_VALIDATION)
    public void handleAssayValidationRequest(ValidationMessageEnvelope<Assay> envelope) {
        logger.debug("Assay validation request received.");

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = assayHandler.handleValidationRequest(envelope);
        sendResults(singleValidationResultsEnvelope);
    }

    @RabbitListener(queues = Queues.CORE_ASSAYDATA_VALIDATION)
    public void handleAssayDataValidationRequest(ValidationMessageEnvelope<AssayData> envelope) {
        logger.debug("AssayData validation request received.");

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = assayDataHandler.handleValidationRequest(envelope);
        sendResults(singleValidationResultsEnvelope);
    }

    @RabbitListener(queues = Queues.CORE_SAMPLE_VALIDATION)
    public void handleSampleValidationRequest(ValidationMessageEnvelope<Sample> envelope) {
        logger.debug("Sample validation request received.");

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = sampleHandler.handleValidationRequest(envelope);
        sendResults(singleValidationResultsEnvelope);
    }

    @RabbitListener(queues = Queues.CORE_STUDY_VALIDATION)
    public void handleStudyValidationRequest(ValidationMessageEnvelope<Study> envelope) {
        logger.debug("Study validation request received.");

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = studyHandler.handleValidationRequest(envelope);
        sendResults(singleValidationResultsEnvelope);
    }

    private void sendResults(SingleValidationResultsEnvelope envelope) {
        List<SingleValidationResult> errorResults = envelope.getSingleValidationResults().stream().filter(svr -> svr.getValidationStatus().equals(ValidationStatus.Error)).collect(Collectors.toList());
        if (errorResults.size() > 0) {
            rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_VALIDATION_ERROR, envelope);
        } else {
            rabbitMessagingTemplate.convertAndSend(Exchanges.VALIDATION, RoutingKeys.EVENT_VALIDATION_SUCCESS, envelope);
        }
    }
}
