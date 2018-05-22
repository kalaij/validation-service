package uk.ac.ebi.subs.validator.filereference;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FileReferenceValidationDTO {

    @NonNull
    private Object entityToValidate;

    @NonNull
    private String submissionId;

    @NonNull
    private Integer validationResultVersion;

    @NonNull
    private String validationResultUUID;
}
