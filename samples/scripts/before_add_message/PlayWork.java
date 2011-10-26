package scripts.before_add_message;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;




public class PlayWork implements OperationTrigger {
    private static String PLAY="ff80818131dbce630131dbf74f88007e";
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
            for (SecuredTaskBean task :message.getTask().getChildren()){
                try{
                TriggerManager.getInstance().createMessage(message.getSecure(), task.getId(), PLAY, message.getDescription(), 0L, task.getHandlerUserId(), task.getHandlerGroupId(), null, null, task.getDeadline(), task.getBudget(), null, true, null);
                    } catch (GranException ge){

                }
        }
            return message;

    }

}
