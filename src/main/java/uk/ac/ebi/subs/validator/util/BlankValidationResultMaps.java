package uk.ac.ebi.subs.validator.util;

import uk.ac.ebi.subs.validator.data.ValidationAuthor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rolando on 19/06/2017.
 * Modified by karoly on 05/07/2017.
 */
public class BlankValidationResultMaps {

    private static final List<ValidationAuthor> SAMPLE_VALIDATION_SERVICES_REQUIRED =
            Arrays.asList(ValidationAuthor.Biosamples, ValidationAuthor.Ena, ValidationAuthor.Core, ValidationAuthor.Taxonomy);
    private static final List<ValidationAuthor> STUDY_VALIDATION_SERVICES_REQUIRED =
            Arrays.asList(ValidationAuthor.Core, ValidationAuthor.Ena);
    private static final List<ValidationAuthor> ASSAY_VALIDATION_SERVICES_REQUIRED =
            Arrays.asList(ValidationAuthor.Core, ValidationAuthor.Ena);
    private static final List<ValidationAuthor> ASSAY_DATA_VALIDATION_SERVICES_REQUIRED =
            Arrays.asList(ValidationAuthor.Core, ValidationAuthor.Ena);

    /**
     * Generates an initial default or "blank" map to hold results of validation outcomes required for
     * BioSample submissions
     *
     * @return an initialised mapping from the required Validation service(ValidationAuthor) to it's respected validation result(boolean, false by default)
     */
    public static Map<ValidationAuthor, Boolean> forSample() {
        return generateDefaultMap(SAMPLE_VALIDATION_SERVICES_REQUIRED);
    }

    public static Map<ValidationAuthor, Boolean> forStudy() {
        return generateDefaultMap(STUDY_VALIDATION_SERVICES_REQUIRED);
    }

    public static Map<ValidationAuthor, Boolean> forAssay() {
        return generateDefaultMap(ASSAY_VALIDATION_SERVICES_REQUIRED);
    }

    public static Map<ValidationAuthor, Boolean> forAssayData() {
        return generateDefaultMap(ASSAY_DATA_VALIDATION_SERVICES_REQUIRED);
    }

    private static Map<ValidationAuthor, Boolean> generateDefaultMap(List<ValidationAuthor> validationAuthors) {
        Map<ValidationAuthor, Boolean> blankValidationResultMap = new HashMap<>();

        for(ValidationAuthor author: validationAuthors) {
            blankValidationResultMap.put(author, false);
        }

        return blankValidationResultMap;
    }
}
