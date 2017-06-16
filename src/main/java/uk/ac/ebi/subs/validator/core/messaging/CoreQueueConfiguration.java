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
    public Queue coreValidationQueue() {
        return new Queue(Queues.CORE_SAMPLE_VALIDATION, true);
    }

    @Bean
    public Binding coreValidationBinding(Queue coreValidationQueue, TopicExchange validationExchange) {
        return BindingBuilder.bind(coreValidationQueue).to(validationExchange).with(RoutingKeys.EVENT_CORE_VALIDATION);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new MappingJackson2MessageConverter();
    }
}
