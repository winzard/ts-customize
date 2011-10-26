package scripts.after_create_task;

import java.util.ArrayList;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUserBean;

public class ChooseParticipants extends
		scripts.before_add_message.ChooseParticipants implements TaskTrigger {

	@Override
	public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task)
			throws GranException {
		ArrayList<SecuredUserBean> users = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(task.getSecure(), task.getId());
        
        String fellowsUDF = task.getUdfValue(WORKGROUP_UDF);
        if (fellowsUDF != null) {
            String[] value = fellowsUDF.split(";");
            if (value != null && value.length > 0)
                for (String login : value) {
                    if (login.length() > 0) {
                        SecuredUserBean f = AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(task.getSecure(), login.substring(1)); //skip @
                        if (f != null) {
                            subscribe(f, task);
                            users.remove(f);
                        }
                    }
                }
            for (SecuredUserBean u: users)
            unsubscribe(u, task);
        }

        return task;
	}

}
