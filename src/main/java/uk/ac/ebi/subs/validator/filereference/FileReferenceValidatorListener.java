package uk.ac.ebi.subs.validator.filereference;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.validator.messaging.FileReferenceQueues.FILE_REFERENCE_ASSAYDATA_VALIDATION;
import static uk.ac.ebi.subs.validator.messaging.FileReferenceRoutingKeys.EVENT_VALIDATION_ERROR;
import static uk.ac.ebi.subs.validator.messaging.FileReferenceRoutingKeys.EVENT_VALIDATION_SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileReferenceValidatorListener {

    @NonNull
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @NonNull
    private FileReferenceHandler fileReferenceHandler;

    @RabbitListener(queues = FILE_REFERENCE_ASSAYDATA_VALIDATION)
    public void handleAssayDataFileReferenceValidationRequest(AssayDataValidationMessageEnvelope envelope) {
        log.debug("AssayData file reference validation request received with ID: {}.",
                envelope.getEntityToValidate().getId());

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = fileReferenceHandler.handleValidationRequest(envelope);
        sendResults(singleValidationResultsEnvelope);
    }

    private void sendResults(SingleValidationResultsEnvelope envelope) {
        List<SingleValidationResult> errorResults = envelope.getSingleValidationResults().stream()
                .filter(svr -> svr.getValidationStatus().equals(SingleValidationResultStatus.Error)).collect(Collectors.toList());
        if (errorResults.size() > 0) {
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_VALIDATION_ERROR, envelope);
        } else {
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_VALIDATION_SUCCESS, envelope);
        }
    }

}
