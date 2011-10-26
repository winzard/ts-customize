package scripts.before_add_message;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;




public class StopWork implements OperationTrigger {
    private static String STOP ="ff80818131e179ef0131e18c2b560041";
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
            for (SecuredTaskBean task :message.getTask().getChildren()){
                try{
                TriggerManager.getInstance().createMessage(message.getSecure(), task.getId(), STOP, message.getDescription(), 0L, task.getHandlerUserId(), task.getHandlerGroupId(), "ff80818131e179ef0131e18e91d3005a", null, task.getDeadline(), task.getBudget(), null, true, null);
                } catch (GranException ge){

                }
        }
            return message;

    }

}
