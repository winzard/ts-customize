package scripts.before_edit_task;

import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;

import java.util.Calendar;

public class CheckDeadline implements TaskTrigger {
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean securedTaskTriggerBean) throws GranException {
        Calendar deadline = securedTaskTriggerBean.getDeadline();
        Calendar now = Calendar.getInstance();
        if (deadline == null || deadline.before(now)) throw new UserException("Укажите срок выполнения задачи");
        return securedTaskTriggerBean;
    }
}
