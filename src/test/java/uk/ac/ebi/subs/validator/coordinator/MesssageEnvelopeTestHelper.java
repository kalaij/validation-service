package uk.ac.ebi.subs.validator.coordinator;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.status.SubmissionStatusEnum;
import uk.ac.ebi.subs.repository.model.Submission;
import uk.ac.ebi.subs.repository.model.SubmissionStatus;
import uk.ac.ebi.subs.repository.repos.SubmissionRepository;
import uk.ac.ebi.subs.repository.repos.status.SubmissionStatusRepository;

import java.util.UUID;

public class MesssageEnvelopeTestHelper {

    static Submission saveNewSubmission(SubmissionStatusRepository submissionStatusRepository, SubmissionRepository submissionRepository, Team team) {
        Submission submssion = new Submission();
        submssion.setId(UUID.randomUUID().toString());

        submssion.setTeam(team);

        submssion.setSubmissionStatus(new SubmissionStatus(SubmissionStatusEnum.Draft));
        submssion.getSubmissionStatus().setTeam(team);

        submissionStatusRepository.insert(submssion.getSubmissionStatus());
        submissionRepository.save(submssion);
        return submssion;
    }

    static Team createTeam () {
        Team team = new Team();
        team.setName(UUID.randomUUID().toString());
        return team;
    }


}
