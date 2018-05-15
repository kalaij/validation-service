package uk.ac.ebi.subs.validator.messaging;

/**
 * This class holds the routing keys for validators route the validation results.
 * These routing keys are bound to the queue the Aggregator listens to VALIDATION_RESULT
 * {@see uk.ac.ebi.subs.validator.messaging.AggregatorMessagingConfiguration}
 */
public class ValidatorsCommonRoutingKeys {

    public static final String EVENT_VALIDATION_SUCCESS = "validation.success";

    public static final String EVENT_VALIDATION_ERROR = "validation.error";
}
