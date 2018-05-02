package uk.ac.ebi.subs.validator.filereference;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.repository.model.AssayData;
import uk.ac.ebi.subs.repository.model.fileupload.File;
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

    public SingleValidationResultsEnvelope handleValidationRequestForUploadedFile(FileReferenceValidationDTO validationDTO,
                                                                                  FileReferenceValidationType fileReferenceValidationType) {

        File fileToValidate = (File)validationDTO.getEntityToValidate();
        List<SingleValidationResult> validationResult = fileReferenceValidator.validate(fileToValidate);

        return processValidationResult(validationResult, validationDTO, fileReferenceValidationType);
    }

    public SingleValidationResultsEnvelope handleValidationRequestForSubmittable(FileReferenceValidationDTO validationDTO,
                                                                           FileReferenceValidationType fileReferenceValidationType) {

        AssayData entityToValidate = (AssayData)validationDTO.getEntityToValidate();
        List<SingleValidationResult> validationResult = fileReferenceValidator.validate(entityToValidate);

        return processValidationResult(validationResult, validationDTO, fileReferenceValidationType);
    }

    private SingleValidationResultsEnvelope processValidationResult(List<SingleValidationResult> validationResult,
                                                                    FileReferenceValidationDTO validationDTO,
                                                                    FileReferenceValidationType fileReferenceValidationType) {
        List<SingleValidationResult> interestingResults = validationResult.stream()
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
