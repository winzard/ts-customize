package scripts.instead_of_add_message;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.*;


import java.util.List;

/**
 * Копирует ACL из задачи, указанной в описании сообщения, в текущую.
 */
public class CopyAccessRules  implements OperationTrigger {

       
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException
    {
        String taskNum = message.getDescription();
        SessionContext sc = message.getSecure();
        SecuredTaskBean task = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, taskNum);
        if (task!=null){
        List<SecuredTaskAclBean> targetAcl = AdapterManager.getInstance().getSecuredAclAdapterManager().getTaskAclList(sc,
                message.getTaskId());
        List<SecuredTaskAclBean> sourceAcl = AdapterManager.getInstance().getSecuredAclAdapterManager().getTaskAclList(sc,
                task.getId());

        for (SecuredTaskAclBean tb: sourceAcl){
            if (tb.canManage() && tb.getTaskId().equals(task.getId())){

                SecuredPrstatusBean group = tb.getGroup();
                SecuredUserBean user = tb.getUser();
                SecuredPrstatusBean prstatus = tb.getPrstatus();
                boolean isOverride = tb.isOverride();
            boolean exists = false;
                if (!sc.getUser().equals(user)){
            for (SecuredTaskAclBean ta: targetAcl){
                SecuredPrstatusBean groupA = ta.getGroup();
                SecuredUserBean userA = ta.getUser();
                SecuredPrstatusBean prstatusA = ta.getPrstatus();
                boolean isOverrideA = ta.isOverride();
                if ((group!=null && groupA!=null && group.equals(groupA)) || (group==null && groupA == null))
                    if ((user!=null && userA!=null && user.equals(userA)) || (user==null && userA == null))
                        if ((prstatus!=null && prstatusA!=null && prstatus.equals(prstatusA)) || (prstatus==null && prstatusA == null))
                            if (isOverride==isOverrideA){
                                exists = true;
                                break;
                            }
            }
                if (!exists){
                    // create
                    String aclid = AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, message.getTaskId(), null,
                            user!=null ? user.getId() : null, group!=null ? group.getId() : null);
                     AdapterManager.getInstance().getSecuredAclAdapterManager().updateTaskAcl(sc, aclid, prstatus.getId(), isOverride);

                }
            }
        }
        }
        }
        return message;
    }
}
