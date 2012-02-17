package scripts.before_add_message;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.kernel.cache.TaskAction;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Скрипт позволяет давать указанным пользователям специальный доступ к
 * конкретной задаче.<br/>
 * Например, у нас есть менеджер и простые пользователи. У менеджера есть доступ
 * на проект с соответствующей ролью, у простых пользователей также есть доступ
 * к проекту, но с ограниченной ролью "Пользователь", которая не позволяет
 * проводить какие-либо изменения в задаче. Указав при редактировании задачи,
 * либо при выполнении какой-нибудь операции в поле "Исполнители" нужных
 * пользователей, менеджер предоставляет им расширенный доступ с ролью
 * WORKFLOW_ROLE для внесения изменений. Тот же самый механизм может
 * использоваться, например, для организации подписки пользователей на задачу. В
 * таком случае нужно организовать рассылку уведомлений всем пользователям с
 * этой ролью.
 * 
 * @author winzard
 * 
 */
public class ChooseParticipants implements OperationTrigger {
	protected String WORKGROUP_UDF = null;
	protected String WORKGROUP_ROLE = null;

	protected static Properties properties = null;

	{
		try {
			properties = new Properties();
			properties.load(new FileReader("tscustomize.properties"));
		} catch (IOException e) {
			properties = null;

		}
	}

	public ChooseParticipants() {
		if (properties != null) {
			WORKGROUP_UDF = properties.getProperty("workgroup.udf");
			WORKGROUP_ROLE = properties.getProperty("workgroup.role.id");
		}
	}

	public void grant(SecuredUserBean member, SecuredTaskBean task)
			throws GranException {
		SessionContext sc = member.getSecure();
		//if (sc.canAction(TaskAction.manageTaskACLs, task.getId())) {
			if (task != null) {
				List<SecuredTaskAclBean> targetAcl = AdapterManager
						.getInstance().getSecuredAclAdapterManager()
						.getTaskAclList(sc, task.getId());
				boolean exists = false;
				for (SecuredTaskAclBean tb : targetAcl) {
					if (tb.canManage() && tb.getTaskId().equals(task.getId())) {
						SecuredUserBean user = tb.getUser();
						SecuredPrstatusBean prstatus = tb.getPrstatus();
						exists = (user.equals(member) && prstatus.getId()
								.equals(WORKGROUP_ROLE));
						if (exists)
							break;
					}
				}
				if (!exists) {
					// create
					String aclid = KernelManager.getAcl().createAcl(task.getId(), null, member.getId(), null, sc.getUserId(null));
	                KernelManager.getAcl().updateAcl(aclid, WORKGROUP_ROLE, false);
				}
			}
		//}
	}

	public void decline(SecuredUserBean member, SecuredTaskBean task)
			throws GranException {
		SessionContext sc = member.getSecure();
		//if (sc.canAction(TaskAction.manageTaskACLs, task.getId())) {
			if (task != null) {
				List<SecuredTaskAclBean> targetAcl = AdapterManager
						.getInstance().getSecuredAclAdapterManager()
						.getTaskAclList(sc, task.getId());
				boolean exists = false;
				for (SecuredTaskAclBean tb : targetAcl) {
					if (tb.canManage() && tb.getTaskId().equals(task.getId())) {
						SecuredUserBean user = tb.getUser();
						SecuredPrstatusBean prstatus = tb.getPrstatus();
						exists = (user.equals(member) && prstatus.getId()
								.equals(WORKGROUP_ROLE));
						if (exists) {
							KernelManager.getAcl().deleteAcl(tb.getId());
							break;
						}
					}
				}
			}
		//}
	}

	public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message)
			throws GranException {
		if (WORKGROUP_UDF==null || WORKGROUP_ROLE==null) throw new UserException("Вы должны указать значения workgroup.udf и workgroup.role.id в файле tscustomize.properties", false);
		ArrayList<SecuredUserBean> users = AdapterManager.getInstance()
				.getSecuredAclAdapterManager()
				.getUserList(message.getSecure(), message.getTaskId());
		SecuredTaskBean task = message.getTask();
		String fellowsUDF = message.getUdfValue(WORKGROUP_UDF);
		if (fellowsUDF != null) {
			String[] value = fellowsUDF.split(";");
			if (value != null && value.length > 0)
				for (String login : value) {
					if (login.length() > 0) {
						SecuredUserBean f = AdapterManager
								.getInstance()
								.getSecuredUserAdapterManager()
								.findByLogin(message.getSecure(),
										login.substring(1)); // skip @
						if (f != null) {
							grant(f, task);
							users.remove(f);
						}
					}
				}
			for (SecuredUserBean u : users)
				decline(u, task);
		}

		return message;
	}

}
