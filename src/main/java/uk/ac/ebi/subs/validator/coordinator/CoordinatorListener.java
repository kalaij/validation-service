package uk.ac.ebi.subs.validator.coordinator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.fileupload.File;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.data.AssayDataValidationEnvelopeToCoordinator;
import uk.ac.ebi.subs.validator.data.AssayValidationEnvelopeToCoordinator;
import uk.ac.ebi.subs.validator.data.FileUploadValidationEnvelopeToCoordinator;
import uk.ac.ebi.subs.validator.data.ProjectValidationEnvelopeToCoordinator;
import uk.ac.ebi.subs.validator.data.SampleValidationEnvelopeToCoordinator;
import uk.ac.ebi.subs.validator.data.StudyValidationEnvelopeToCoordinator;

import static uk.ac.ebi.subs.validator.messaging.CoordinatorQueues.FILE_REF_VALIDATOR;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorQueues.SUBMISSION_ASSAY_DATA_VALIDATOR;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorQueues.SUBMISSION_ASSAY_VALIDATOR;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorQueues.SUBMISSION_PROJECT_VALIDATOR;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorQueues.SUBMISSION_SAMPLE_VALIDATOR;
import static uk.ac.ebi.subs.validator.messaging.CoordinatorQueues.SUBMISSION_STUDY_VALIDATOR;

@Component
@RequiredArgsConstructor
public class CoordinatorListener {
    private static final Logger logger = LoggerFactory.getLogger(CoordinatorListener.class);

    @NonNull
    private SubmittableHandler submittableHandler;
    @NonNull
    private FileValidationRequestHandler fileValidationRequestHandler;
    @NonNull
    private ChainedValidationService chainedValidationService;

    /**
     * Project validator data entry point.
     * @param envelope contains the {@link Project} entity to validate
     */
    @RabbitListener(queues = SUBMISSION_PROJECT_VALIDATOR)
    public void processProjectSubmission(ProjectValidationEnvelopeToCoordinator envelope) {
        Project project = envelope.getEntityToValidate();

        if (project == null) {
            throw new IllegalArgumentException("The envelop should contain a project.");
        }

        logger.info("Received validation request on project {}", project.getId());

        if (!submittableHandler.handleSubmittable(project)) {
            logger.error("Error handling project with id {}", project.getId());
        } else {
            logger.trace("Triggering chained validation from project {}", project.getId());
            chainedValidationService.triggerChainedValidation(project, envelope.getSubmissionId());
        }
    }

    /**
     * Sample validator data entry point.
     * @param envelope contains the {@link Sample} entity to validate
     */
    @RabbitListener(queues = SUBMISSION_SAMPLE_VALIDATOR)
    public void processSampleSubmission(SampleValidationEnvelopeToCoordinator envelope) {
        Sample sample = envelope.getEntityToValidate();

        if (sample == null) {
            throw new IllegalArgumentException("The envelop should contain a sample.");
        }

        logger.info("Received validation request on sample with id {}", sample.getId());

        if (!submittableHandler.handleSubmittable(sample, envelope.getSubmissionId())) {
            logger.error("Error handling sample with id {}", sample.getId());
        } else {
            logger.trace("Triggering chained validation from sample {}", sample.getId());
            chainedValidationService.triggerChainedValidation(sample, envelope.getSubmissionId());
        }
    }

    /**
     * Study validator data entry point.
     * @param envelope contains the {@link Study} entity to validate
     */
    @RabbitListener(queues = SUBMISSION_STUDY_VALIDATOR)
    public void processStudySubmission(StudyValidationEnvelopeToCoordinator envelope){
        Study study = envelope.getEntityToValidate();

        if (study == null) {
            throw new IllegalArgumentException("The envelop should contain a study.");
        }

        logger.info("Received validation request on study with id {}", study.getId());

        if (!submittableHandler.handleSubmittable(study,envelope.getSubmissionId())) {
            logger.error("Error handling study with id {}", study.getId());
        } else {
            logger.trace("Triggering chained validation from study {}", study.getId());
            chainedValidationService.triggerChainedValidation(study, envelope.getSubmissionId());
        }
    }

    /**
     * Assay validator data entry point.
     * @param envelope contains the {@link Assay} entity to validate
     */
    @RabbitListener(queues = SUBMISSION_ASSAY_VALIDATOR)
    public void processAssaySubmission(AssayValidationEnvelopeToCoordinator envelope) {
        Assay assay = envelope.getEntityToValidate();

        if (assay == null) {
            throw new IllegalArgumentException("The envelop should contain an assay.");
        }

        logger.info("Received validation request on assay {}", assay.getId());

        if (!submittableHandler.handleSubmittable(assay,envelope.getSubmissionId())) {
            logger.error("Error handling assay with id {}", assay.getId());
        } else {
            logger.trace("Triggering chained validation from assay {}", assay.getId());
            chainedValidationService.triggerChainedValidation(assay, envelope.getSubmissionId());
        }
    }

    /**
     * AssayData validator data entry point.
     * @param envelope contains the {@link AssayData} entity to validate
     */
    @RabbitListener(queues = SUBMISSION_ASSAY_DATA_VALIDATOR)
    public void processAssayDataSubmission(AssayDataValidationEnvelopeToCoordinator envelope) {
        AssayData assayData = envelope.getEntityToValidate();

        if (assayData == null) {
            throw new IllegalArgumentException("The envelop should contain an assay data.");
        }

        logger.info("Received validation request on assay data {}", assayData.getId());

        if (!submittableHandler.handleSubmittable(assayData,envelope.getSubmissionId())) {
            logger.error("Error handling assayData with id {}", assayData.getId());
        } else {
            logger.trace("Triggering chained validation from assayData {}", assayData.getId());
            chainedValidationService.triggerChainedValidation(assayData, envelope.getSubmissionId());
        }
    }

    /**
     * File reference existence validator data entry point.
     * @param envelope contains the {@link File} entity to validate
     */
    @RabbitListener(queues = FILE_REF_VALIDATOR)
    public void processFileReferenceValidationRequest(FileUploadValidationEnvelopeToCoordinator envelope) {
        File fileToValidate = envelope.getFileToValidate();

        if (fileToValidate == null) {
            throw new IllegalArgumentException("The envelop should contain a file to validate.");
        }

        logger.info("Received validation request on file [id: {}]", fileToValidate.getId());

        if (!fileValidationRequestHandler.handleFile(fileToValidate, envelope.getSubmissionId())) {
            logger.error("Error handling file to validate with id {}", fileToValidate.getId());
        }
    }
}
