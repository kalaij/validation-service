package uk.ac.ebi.subs.validator.coordinator.messages;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StoredSubmittableDeleteMessage {
    @NonNull
    private String submissionId;
}
