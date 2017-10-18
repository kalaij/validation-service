package uk.ac.ebi.subs.validator.coordinator.config;

/**
 * This is just a marker interface for tests depends on a running MongoDB instance.
 * We can use this marker interface for integration tests that we don't want to execute on CI server.
 *
 * Created by karoly on 05/07/2017.
 */
public interface MongoDBDependentTest {
}
