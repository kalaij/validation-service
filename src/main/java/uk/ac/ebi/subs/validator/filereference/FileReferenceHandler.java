package uk.ac.ebi.subs.validator.filereference;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.fileupload.File;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.validator.core.validators.ValidatorHelper;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;
import uk.ac.ebi.subs.validator.util.ValidationHelper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.validator.util.ValidationHelper.generateSingleValidationResultsEnvelope;

@Service
@RequiredArgsConstructor
public class FileReferenceHandler {

    @NonNull
    private FileReferenceValidator fileReferenceValidator;

    public SingleValidationResultsEnvelope handleValidationRequest(FileReferenceValidationDTO validationDTO,
                                                                   FileReferenceValidationType fileReferenceValidationType) {
        String submissionId = validationDTO.getSubmissionId();

        List<SingleValidationResult> interestingResults = fileReferenceValidator.validate(submissionId).stream()
                .filter(ValidationHelper::statusIsNotPassOrPending)
                .collect(Collectors.toList());

        if (interestingResults.isEmpty()) {
            String objectToValidateID =
                    fileReferenceValidationType == FileReferenceValidationType.ASSAY_DATA ?
                            ((AssayData)validationDTO.getEntityToValidate()).getId() :
                            ((File)validationDTO.getEntityToValidate()).getId();

            SingleValidationResult r = ValidatorHelper.getDefaultSingleValidationResult(objectToValidateID);
            r.setValidationStatus(SingleValidationResultStatus.Pass);
            interestingResults = Collections.singletonList(r);
        }

        return generateSingleValidationResultsEnvelope(validationDTO.getValidationResultVersion(),
                validationDTO.getValidationResultUUID(), interestingResults, ValidationAuthor.FileReference);
    }
}
