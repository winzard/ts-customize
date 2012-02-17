package scripts.after_create_task;

import java.util.ArrayList;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUserBean;

public class ChooseParticipants extends
		scripts.before_add_message.ChooseParticipants implements TaskTrigger {

	@Override
	public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task)
			throws GranException {
		if (WORKGROUP_UDF==null || WORKGROUP_ROLE==null) throw new UserException("Вы должны указать значения workgroup.udf и workgroup.role.id в файле tscustomize.properties", false);
		ArrayList<SecuredUserBean> users = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(task.getSecure(), task.getId());
        
        String fellowsUDF = task.getUdfValue(WORKGROUP_UDF);
        if (fellowsUDF != null) {
            String[] value = fellowsUDF.split(";");
            if (value != null && value.length > 0)
                for (String login : value) {
                    if (login.length() > 0) {
                        SecuredUserBean f = AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(task.getSecure(), login.substring(1)); //skip @
                        if (f != null) {
                            grant(f, task);
                            users.remove(f);
                        }
                    }
                }
            for (SecuredUserBean u: users)
            decline(u, task);
        }

        return task;
	}

}
