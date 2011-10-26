package scripts.before_add_message;

import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;

public class CheckFinished implements OperationTrigger {

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
       for (SecuredTaskBean task : message.getTask().getChildren()) {
           if (!task.getStatus().isFinish()) throw new UserException("Сначала должны быть завершены все мероприятия", false);
       }
        return message;
    }
}

