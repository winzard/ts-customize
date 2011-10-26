package scripts.before_add_message;

import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.OperationTrigger;
import scripts.CommonScrum;

/**
 * Проверяет, запущен ли спринт
 */
public class CheckSprintRunning  extends CommonScrum implements OperationTrigger {

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {


        String runState = SCRUM_SPRINT_STATE_RUN;
        if (message.getTask().getParent().getStatusId().equals(runState))
            throw new UserException("Сначала остановите спринт");
        return message;
    }
}
