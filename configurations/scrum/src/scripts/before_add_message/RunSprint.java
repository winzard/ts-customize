package scripts.before_add_message;

import scripts.CommonScrum;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.app.TriggerManager;

import java.util.HashMap;
/**
 * Запускает спринт, переводя все задачи в состояние "запущено". Спринт не запустится, если бюждет не определен или превышен.
 * Поэтому before trigger, а не after.
 */
public class RunSprint extends CommonScrum implements OperationTrigger {

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        SecuredTaskBean task1 = message.getTask();
        HashMap<String, String> udf = new HashMap<String, String>();
        udf.put("Спринт", task1.getName() + " [#"+task1.getNumber()+"]");
        String readyState = SCRUM_ITEM_STATE_READY;
        String planOperation = SCRUM_ITEM_OPERATION_RUN;
        

        KernelManager.getFind().findMstatus(planOperation);
        Long budget = task1.getBudget();
        Long calculatedBudget = 0L;
        for (SecuredTaskBean task :message.getTask().getChildren()){
            calculatedBudget+=task.getBudget();
        }
         if (budget>0){
        if (budget>=calculatedBudget){
            
            for (SecuredTaskBean task :message.getTask().getChildren()){
            if (task.getStatusId().equals(readyState))
                TriggerManager.getInstance().createMessage(message.getSecure(), task.getId(), planOperation, message.getDescription(), 0L, task.getHandlerUserId(), task.getHandlerGroupId(), null, null, message.getDeadline()==null?  task1.getDeadline() : message.getDeadline(), task.getBudget(), udf, false, null);

        }
            return message;
        } else {
            throw new UserException("Бюджет спринта меньше бюджета входящих в него задач. Переопределите бюджет или состав спринта");
        }
         }
        else throw new UserException("Сначала определите бюджет спринта");

    }
}
