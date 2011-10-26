package scripts.before_create_task;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUserBean;

/**
 * Скрипт меняет Автора (Submitter) ЗАДАЧИ на указанного в дополнительном поле
 * типа "Пользователь". Поле либо должно называться "Отправитель", либо его
 * название должно быть прописано в пареметре <b>change.submitter.udf</b> в
 * properties-файле trackstudio.changesubmitter.udf<br/> 
 * Файл должен располагаться в
 * корневой папке TrackStudio.<br/>
 * change.submitter.udf=Отправитель
 */
public class ChangeSubmitter implements TaskTrigger {
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
	private static String CHANGE_SUBMITTER_UDF = "Отправитель";
	public ChangeSubmitter() {
		/*
		 * Если прочитался файл, загружаем оттуда свойства
		 */
		if (properties != null) {
			CHANGE_SUBMITTER_UDF = properties
					.getProperty("change.submitter.udf");
		}
	}

	

	public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task)
			throws GranException {
		// берем значение из поля 
		String udf = task.getUdfValue(CHANGE_SUBMITTER_UDF);
		if (udf != null) {
			String[] value = udf.split(";");
			SecuredUserBean f = AdapterManager.getInstance()
					.getSecuredUserAdapterManager()
					.findByLogin(task.getSecure(), value[0].substring(1)); // skip
			// если значение установлено, меняем автора																// @
			if (f!=null) task.setSubmitterId(f.getId());
		}
		return task;

	}
}
