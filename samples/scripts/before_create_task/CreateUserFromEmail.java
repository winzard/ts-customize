package scripts.before_create_task;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.tools.Null;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Скрипт предназначен для импорта в систему писем от ранее незарегистрированных
 * пользоватетелей. <br/>
 * Он определяет автора по полю From в описании (оно
 * генерируется автоматически при импорте писем, создает пользователя, соответствующего этим данным и меняет Автора (Submitter)
 * задачи<br/>
 * Значение id роли, с которой будет создаваться пользователь, должно быть прописано в
 * пареметре <b>submitter.role.id</b> в properties-файле trackstudio.changesubmitter.udf 
 * Файл должен располагаться в корневой папке
 * submitter.role.id=d14883c11eef1ae9011eef360b6f0045
 */
public class CreateUserFromEmail implements TaskTrigger {
	private static Properties properties = null;
	/*
	 * Читаем файл со свойствами
	 */
	{
		try {
			properties = new Properties();
			properties.load(new FileReader("tscustomize.properties"));
		} catch (IOException e) {
			properties = null;

		}
	}
	private static String SUBMITTER_ROLE_ID = "d14883c11eef1ae9011eef360b6f0045";
	public CreateUserFromEmail() {
		/*
		 * Если прочитался файл, загружаем оттуда свойства
		 */
		if (properties != null) {
			SUBMITTER_ROLE_ID = properties
					.getProperty("submitter.role.id");
		}
	}

	public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task)
			throws GranException {
		String s = task.getDescription();

		
		// String userStatusId = task.getSubmitter().getPrstatusId();
		if (s != null && s.length() > 0) {
			String emailPattern = "From:\\s*\\\"?(\\S+\\s*\\S+[^\\\"])?\\\"?\\s+(<|&lt;)?(([-a-z0-9!#$%&'*+/=?^_`{|}~]+(\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*)@([a-z0-9.]+))(&gt;|>)?";
			// From: max.vasenkov@gmail.com
			// From: Maxim Vasenkov <max.vasenkov@gmail.com>
			// From: Maxim Vasenkov &lt;max.vasenkov@gmail.com&gt;
			// From: Winzard <i@winzard.ru>
			// From: Admin <admin@localhost>
			// From: "Максим Васенков" <vasenkov@any.place.com>
			// From: "Максим Васенков" &lt;vasenkov@any.place.com&gt;
			Pattern pat = Pattern.compile(emailPattern);
			Matcher mat = pat.matcher(s);

			if (mat.find()) {
				String userName = mat.group(1);
				String userEmail = mat.group(3);
				if (userName == null)
					userName = mat.group(4);
				String fId = KernelManager.getUser()
						.findUserIdByEmailNameProject(userEmail, userEmail,
								task.getParentId());
				if (fId == null) {
					String id = AdapterManager
							.getInstance()
							.getSecuredUserAdapterManager()
							.createUser(task.getSecure(),
									Null.beNull(task.getSubmitterId()),
									userEmail, userName,
									Null.beNull(SUBMITTER_ROLE_ID));
					AdapterManager
							.getInstance()
							.getSecuredUserAdapterManager()
							.updateUser(task.getSecure(), id, userEmail,
									userName, null, userEmail, SUBMITTER_ROLE_ID,
									task.getSubmitterId(),
									task.getSecure().getUser().getTimezone(),
									task.getSecure().getUser().getLocale(),
									task.getSecure().getUser().getCompany(),
									null, null, null, null, true);
					String aclid = AdapterManager
							.getInstance()
							.getSecuredAclAdapterManager()
							.createAcl(task.getSecure(), task.getParentId(),
									null, id, null);
					AdapterManager
							.getInstance()
							.getSecuredAclAdapterManager()
							.updateTaskAcl(task.getSecure(), aclid,
									SUBMITTER_ROLE_ID, false);
					if (id != null)
						task.setSubmitterId(id);
				}
			}

		}
		return task;
	}
}
