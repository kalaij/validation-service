# Validatoion Service
[![Build Status](https://travis-ci.org/EMBL-EBI-SUBS/validation-service.svg?branch=master)](https://travis-ci.org/EMBL-EBI-SUBS/validation-service)

This service responsible for the validation workflow in the Unified Submission Interface.
It has got the entry point for the USI validation infrastructure. All validation requests have to be routed trough the validation service.
Its subs-systems are:
  - Validator-coordinator
  - Validation-aggregator
  - validation-status-flipper
  - and the various validators (core, taxon, biosample, etc)

## License
This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details
