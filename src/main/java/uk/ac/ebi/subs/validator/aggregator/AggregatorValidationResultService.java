package uk.ac.ebi.subs.validator.aggregator;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationResult;

@Service
public class AggregatorValidationResultService {

    private MongoTemplate mongoTemplate;

    public AggregatorValidationResultService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public boolean updateValidationResult(SingleValidationResultsEnvelope envelope) {

        Query query = new Query(Criteria.where("_id").is(envelope.getValidationResultUUID())
                .and("version").is(envelope.getValidationResultVersion()));

        Update update = new Update().set("expectedResults." + envelope.getValidationAuthor(), envelope.getSingleValidationResults());

        ValidationResult validationResult = mongoTemplate.findAndModify(query, update, ValidationResult.class);

        if(validationResult != null) {
            return true;
        } else {
            return false;
        }
    }

}