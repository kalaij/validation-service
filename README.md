# Validation Service
[![Build Status](https://travis-ci.org/EMBL-EBI-SUBS/validation-service.svg?branch=master)](https://travis-ci.org/EMBL-EBI-SUBS/validation-service)

This service responsible for the validation workflow in the Unified Submission Interface.

Its subs-systems are:
  - Validator-coordinator
  
    It has got the entry point for the USI validation infrastructure.
    All validation requests have to be routed trough the validation service.
  
  - Validation-aggregator
  
    It waits for messages coming from any validator (ENA, Biosamples, Taxonomy, ...)
    and updates the appropriate Validation Result Document.
  
  - validation-status-flipper

    This service is listening on events on the validation aggregation results Queue.
    When processing a published event it will update the ValidationResult document's status according to the availability of the validation results. If all the entity has been validated, then the status will change to Complete, otherwise it will stay Pending as initially.

  - and the various validators (core, taxon, biosample, etc)

## License
This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details
