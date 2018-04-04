package uk.ac.ebi.subs.validator.filereference;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.repository.model.fileupload.File;
import uk.ac.ebi.subs.repository.repos.fileupload.FileRepository;
import uk.ac.ebi.subs.repository.repos.submittables.AssayDataRepository;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileReferenceValidator {

    @NonNull
    private FileRepository fileRepository;

    @NonNull
    private AssayDataRepository assayDataRepository;

    final static String STORED_FILE_NOT_REFERENCED = "The [%s] uploaded file is not referenced in any of the run.";
    final static String FILE_METADATA_NOT_EXISTS_AS_STORED_FILE = "The [%s] file metadata is not exists as a file in the file storage area.";
    static final String SUCCESS_FILE_VALIDATION_MESSAGE_ASSAY_DATA = "All file(s) that referenced in file metadata exists on the file storage";
    static final String SUCCESS_FILE_VALIDATION_MESSAGE_UPLOADED_FILE = "All file uploaded file(s) referenced in file metadata";

    public List<SingleValidationResult> validate(String submissionID) {
        List<File> storedFiles = fileRepository.findBySubmissionId(submissionID);
        final List<uk.ac.ebi.subs.repository.model.AssayData> assayDataList =
                assayDataRepository.findBySubmissionId(submissionID);

        List<String> filePathsFromMetadata = assayDataList
                .stream().map( AssayData::getFiles).collect(Collectors.toList())
                .stream().flatMap(List::stream).map(uk.ac.ebi.subs.data.component.File::getName)
                .collect(Collectors.toList());

        Map<String, List<String>> filePathsByAssayDataID = new HashMap<>();
        for (AssayData assayData : assayDataList) {
            filePathsByAssayDataID.put(
                assayData.getId(),
                assayData.getFiles().stream().map(uk.ac.ebi.subs.data.component.File::getName).collect(Collectors.toList())
            );
        }

        List<SingleValidationResult> singleValidationResults = new ArrayList<>();

        for (File file : storedFiles) {
            singleValidationResults.add(
                    validateIfStoredFilesReferencedInSubmittables(file, filePathsFromMetadata)
            );
        }

        filePathsByAssayDataID.forEach( (assayDataID, filePaths) -> {
            if (filePaths.size() > 0) {
                for (String filepath : filePaths) {
                    singleValidationResults.add(validateIfReferencedFileExistsOnStorage(assayDataID, filepath,
                            storedFiles.stream().map(File::getTargetPath).collect(Collectors.toList())));
                }
            } else {
                singleValidationResults.add(generateDefaultSingleValidationResult(
                        assayDataID, SUCCESS_FILE_VALIDATION_MESSAGE_ASSAY_DATA));
            }
        } );

        return singleValidationResults;
    }

    private SingleValidationResult validateIfStoredFilesReferencedInSubmittables(
            File storedFile, List<String> filePathsFromMetadata ) {
        SingleValidationResult singleValidationResult = generateDefaultSingleValidationResult(
                storedFile.getId(), SUCCESS_FILE_VALIDATION_MESSAGE_UPLOADED_FILE);

        if (!filePathsFromMetadata.contains(storedFile.getTargetPath())) {
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
            singleValidationResult.setMessage(String.format(STORED_FILE_NOT_REFERENCED, storedFile.getFilename()));
        }

        return singleValidationResult;
    }

    private SingleValidationResult validateIfReferencedFileExistsOnStorage(String assayDataId, String metadataFilePath,
                                                                           List<String> storedFilesPath) {
        SingleValidationResult singleValidationResult = generateDefaultSingleValidationResult(
                assayDataId, SUCCESS_FILE_VALIDATION_MESSAGE_ASSAY_DATA);

        if (!storedFilesPath.contains(metadataFilePath)) {
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
            singleValidationResult.setMessage(String.format(FILE_METADATA_NOT_EXISTS_AS_STORED_FILE, metadataFilePath));
        }

        return singleValidationResult;
    }

    private SingleValidationResult generateDefaultSingleValidationResult(String entityId, String successMessage) {
        SingleValidationResult result = new SingleValidationResult(ValidationAuthor.FileReference, entityId);
        result.setValidationStatus(SingleValidationResultStatus.Pass);
        result.setMessage(successMessage);
        return result;
    }
}
