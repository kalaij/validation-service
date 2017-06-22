package uk.ac.ebi.subs.validator.core.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import uk.ac.ebi.subs.validator.messaging.Queues;
import uk.ac.ebi.subs.validator.messaging.RoutingKeys;
import uk.ac.ebi.subs.validator.messaging.ValidationExchangeConfig;

@Configuration
@ComponentScan(basePackageClasses = ValidationExchangeConfig.class)
public class CoreQueueConfiguration {

    @Bean
    public MessageConverter messageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public Queue coreAssayValidationQueue() {
        return new Queue(Queues.CORE_ASSAY_VALIDATION, true);
    }

    @Bean
    public Binding coreAssayValidationBinding(Queue coreAssayValidationQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(coreAssayValidationQueue).to(validationExchange).with(RoutingKeys.EVENT_CORE_ASSAY_VALIDATION);
    }

    @Bean
    public Queue coreAssayDataValidationQueue() {
        return new Queue(Queues.CORE_ASSAYDATA_VALIDATION, true);
    }

    @Bean
    public Binding coreAssayDataValidationBinding(Queue coreAssayDataValidationQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(coreAssayDataValidationQueue).to(validationExchange).with(RoutingKeys.EVENT_CORE_ASSAYDATA_VALIDATION);
    }

    @Bean
    public Queue coreSampleValidationQueue() {
        return new Queue(Queues.CORE_SAMPLE_VALIDATION, true);
    }

    @Bean
    public Binding coreSampleValidationBinding(Queue coreSampleValidationQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(coreSampleValidationQueue).to(validationExchange).with(RoutingKeys.EVENT_CORE_SAMPLE_VALIDATION);
    }

    @Bean
    public Queue coreStudyValidationQueue() {
        return new Queue(Queues.CORE_STUDY_VALIDATION, true);
    }

    @Bean
    public Binding coreStudyValidationBinding(Queue coreStudyValidationQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(coreStudyValidationQueue).to(validationExchange).with(RoutingKeys.EVENT_CORE_STUDY_VALIDATION);
    }

}
