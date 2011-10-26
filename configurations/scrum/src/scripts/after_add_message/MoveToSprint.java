package scripts.after_add_message;


import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.exception.GranException;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.constants.CommonConstants;
import com.trackstudio.external.OperationTrigger;
import scripts.CommonScrum;

/**
 * Переносит задачу в спринт при выполнении операции, если установлено дополнительное поле "Спринт"
 */
public class MoveToSprint extends CommonScrum implements OperationTrigger{

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        String link = message.getUdfValue("Спринт");
        String category  = SCRUM_SRINT_CATEGORY;
        if (link!=null && link.length()>0){
        int j = link.lastIndexOf("[#");
        if (j>0){
         // переносим в спринт
         String taskNumber = link.substring(j+1, link.length()-1);
         SecuredTaskBean t = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(message.getSecure(),taskNumber);
         if (t!=null && !message.getTask().getParent().equals(t)){
             
             AdapterManager.getInstance().getSecuredTaskAdapterManager().pasteTasks(message.getSecure(), t.getId(), message.getTaskId(), CommonConstants.CUT);

         }

        }
        }
        else {
            //переносим из спринта обратно в продукт бэклог
            if (message.getTask().getParent().getCategoryId().equals(category)){
                AdapterManager.getInstance().getSecuredTaskAdapterManager().pasteTasks(message.getSecure(), message.getTask().getParent().getParentId(), message.getTaskId(), CommonConstants.CUT);
            }
        }
        return message;
    }
}
