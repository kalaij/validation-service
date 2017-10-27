# Validation Service
[![Build Status](https://travis-ci.org/EMBL-EBI-SUBS/validation-service.svg?branch=master)](https://travis-ci.org/EMBL-EBI-SUBS/validation-service)

This is the service responsible for the validation of metadata (and in the near future also files) in the Unified Submission Interface (USI).

The structure of this service is organized in 3 components:

### Coordinator
The entry point for the USI validation infrastructure.
All validation requests have to be routed trough the coordinator.
  
### Aggregator
This component waits for messages coming in from any validator (ENA, Biosamples, Taxonomy, ...) and updates the appropriate Validation Result Document.
  
### Status-flipper
The final component listens for events on the validation aggregation results queue.
When processing an event it will update the ValidationResult document's status according to the availability of the validation results.
If all the validation is done, then the status is changed to `Complete`, otherwise it will remain `Pending`.

## License
This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE.md) file for details.
