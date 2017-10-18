package uk.ac.ebi.subs.validator.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.subs.messaging.ExchangeConfig;
import uk.ac.ebi.subs.messaging.Queues;

import static uk.ac.ebi.subs.validator.messaging.AggregatorQueues.VALIDATION_RESULT;
import static uk.ac.ebi.subs.validator.messaging.AggregatorRoutingKeys.EVENT_VALIDATION_ERROR;
import static uk.ac.ebi.subs.validator.messaging.AggregatorRoutingKeys.EVENT_VALIDATION_SUCCESS;

/**
 * Messaging configuration for the validator aggregator.
 *
 * Created by karoly on 17/07/2017.
 */
@Configuration
@ComponentScan(basePackageClasses = ExchangeConfig.class)
public class AggregatorMessagingConfiguration {

    /**
     * Instantiate a {@link Queue} for publish validation results.
     *
     * @return an instance of a {@link Queue} for publish validation results.
     */
    @Bean
    Queue validationResultQueue() {
        return Queues.buildQueueWithDlx(VALIDATION_RESULT);
    }

    /**
     * Create a {@link Binding} between the validation exchange and validation result queue
     * using the routing key of successful validation.
     *
     * @param validationResultQueue {@link Queue} for validation results
     * @param submissionExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and validation result queue
     * using the routing key of successful validation.
     */
    @Bean
    Binding validationResultSuccessBinding(Queue validationResultQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(validationResultQueue).to(submissionExchange)
                .with(EVENT_VALIDATION_SUCCESS);
    }

    /**
     * Create a {@link Binding} between the validation exchange and validation result queue
     * using the routing key of erred validation.
     *
     * @param validationResultQueue {@link Queue} for validation results
     * @param submissionExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and validation result queue
     * using the routing key of erred validation.
     */
    @Bean
    Binding validationResultErrorBinding(Queue validationResultQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(validationResultQueue).to(submissionExchange)
                .with(EVENT_VALIDATION_ERROR);
    }


}
