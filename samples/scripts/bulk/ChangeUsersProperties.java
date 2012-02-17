package scripts.bulk;

import java.util.ArrayList;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.securedkernel.SecuredUserAdapterManager;

public class ChangeUsersProperties implements TaskBulkProcessor {

	@Override
	public SecuredTaskBean execute(SecuredTaskBean task) throws GranException {
		SecuredUserAdapterManager uam = AdapterManager.getInstance()
				.getSecuredUserAdapterManager();
		ArrayList<SecuredUserBean> users = AdapterManager.getInstance()
				.getSecuredAclAdapterManager()
				.getUserList(task.getSecure(), task.getId());
		for (SecuredUserBean u : users)
			uam.updateUser(task.getSecure(), u.getId(), u.getLogin(),
					u.getName(), u.getTel(), u.getEmail(), u.getPrstatusId(),
					u.getManagerId(), u.getTimezone(), u.getLocale(),
					u.getCompany(), u.getTemplate(), task.getId(),
					u.getExpireDate(), u.getPreferences(), u.isEnabled());

		return task;
	}

}
