package uk.ac.ebi.subs.validator.coordinator.messages;

import lombok.Data;

@Data
public class FileDeletedMessage {

    private String submissionId;
}
