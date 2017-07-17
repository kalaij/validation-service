package uk.ac.ebi.subs.validator.aggregator.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;
import uk.ac.ebi.subs.validator.messaging.ValidationExchangeConfig;

/**
 * Messaging configuration for the validator aggregator.
 *
 * Created by karoly on 17/07/2017.
 */
@Configuration
@ComponentScan(basePackageClasses = ValidationExchangeConfig.class)
public class AggregatorMessagingConfiguration {

    /**
     * Instantiate a JSON message converter.
     *
     * @return an instance of JSON message converter.
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Instantiate a {@link Queue} for publish validation results.
     *
     * @return an instance of a {@link Queue} for publish validation results.
     */
    @Bean
    Queue validationResultQueue() {
        return new Queue(Queues.VALIDATION_RESULT, true);
    }

    /**
     * Create a {@link Binding} between the validation exchange and validation result queue
     * using the routing key of successful validation.
     *
     * @param validationResultQueue {@link Queue} for validation results
     * @param validationExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and validation result queue
     * using the routing key of successful validation.
     */
    @Bean
    Binding validationResultSuccessBinding(Queue validationResultQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(validationResultQueue).to(validationExchange)
                .with(RoutingKeys.EVENT_VALIDATION_SUCCESS);
    }

    /**
     * Create a {@link Binding} between the validation exchange and validation result queue
     * using the routing key of erred validation.
     *
     * @param validationResultQueue {@link Queue} for validation results
     * @param validationExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and validation result queue
     * using the routing key of erred validation.
     */
    @Bean
    Binding validationResultErrorBinding(Queue validationResultQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(validationResultQueue).to(validationExchange)
                .with(RoutingKeys.EVENT_VALIDATION_ERROR);
    }


}
