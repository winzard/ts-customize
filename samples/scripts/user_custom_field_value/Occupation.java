package scripts.user_custom_field_value;

import java.util.ArrayList;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.TaskFilter;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.UserUDFValueScript;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
/**
 * Этот скрипт предназначен для дополнительного поля типа Текст (с отображением в HTML), привязанного к пользователю<br>
 * Он выводит список задач (со ссылками), в которых пользователь является ответственным.<br>
 * Есть альтернативный способ вывода этой же информации: можно сделать дополнительное поле типа Задача, и в качестве значений выводить не String, а List<SecuredTaskBean>
 * Приведенный здесь способ удобнее тем, что вместе со ссылкой на задачу можно выводить и другую информацию по вкусу.
 * @author winzard
 *
 */
public class Occupation implements UserUDFValueScript {
/** Идентификатор фильтра, которым обрабатывается список задач.<br>
 * Создайте свой фильтр и укажите здесь его идентификатор перед компиляцией.<br>
 * В фильтре нужно указать опцию глубокого поиска. Можете также отсеять ненужные, например, закрытые задачи.
 * */
public static final String MY_FILTER="ff80818133205e4c01332163ae960575";
	@Override
	public Object calculate(SecuredUserBean user) throws GranException {
		StringBuffer taskIds = new StringBuffer();
		 TaskFilter taskList = new TaskFilter(new SecuredTaskBean("1", user.getSecure()));
		 // Загружаем параметры из указанного выше фильтра
	     TaskFValue taskFValue = KernelManager.getFilter().getTaskFValue(MY_FILTER);
	     // Меняем или устанавливаем в них параметр поиска по ответственному
	       taskFValue.set(FieldMap.HUSER_NAME.getFilterKey(), user.getId());
	       // Получаем список задач и запихиваем их через двойную решетку в выводимое поле. Обработчик затем преобразует всё это в ссылки
	        ArrayList<SecuredTaskBean> taskCol = taskList.getTaskList(taskFValue, true, false, taskFValue.getSortOrder());
	        if (taskCol!=null && taskCol.size()>0){
	        	taskIds.append("<DIV>");
	       for (SecuredTaskBean task : taskCol) {
	    	   taskIds.append("##"+task.getNumber()).append("<br/>");
	       }
	       taskIds.append("</DIV>");
	        }
	       return taskIds.toString();
	}

}
