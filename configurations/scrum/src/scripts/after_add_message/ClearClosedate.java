package scripts.after_add_message;

import scripts.CommonScrum;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;


/**
 * Стирает Closedate у задачи
 */
public class ClearClosedate extends CommonScrum implements OperationTrigger {
     public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        KernelManager.getTask().updateTaskCloseDate(message.getTaskId(), null);
        return message;
    }
}
