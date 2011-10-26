package scripts.after_add_message;

import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.exception.GranException;
import com.trackstudio.app.TriggerManager;
import com.trackstudio.external.OperationTrigger;
import scripts.CommonScrum;

import java.util.HashMap;

/**
 * Останавливает спринт, переводя подзадачи в состояние остановленных.
 */
public class StopSprint extends CommonScrum implements OperationTrigger{
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        SecuredTaskBean task1 = message.getTask();

        HashMap udf = new HashMap();
        udf.put("Спринт", task1.getName() + " [#"+task1.getNumber()+"]");
        String stopOperation = SCRUM_ITEM_OPERATION_STOP;
        String runState = SCRUM_ITEM_STATE_RUN;
            for (SecuredTaskBean task :message.getTask().getChildren()){
            if (task.getStatusId().equals(runState))
                TriggerManager.getInstance().createMessage(message.getSecure(), task.getId(), stopOperation, message.getDescription(), 0L, task.getHandlerUserId(), task.getHandlerGroupId(), null, null, task1.getDeadline(), task.getBudget(), udf, false, null);
        }
            return message;
        

    }
}
