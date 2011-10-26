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

public class Occupation implements UserUDFValueScript {
public static final String MY_FILTER="ff80818133205e4c01332163ae960575";
	@Override
	public Object calculate(SecuredUserBean user) throws GranException {
		StringBuffer taskIds = new StringBuffer();
		 TaskFilter taskList = new TaskFilter(new SecuredTaskBean("1", user.getSecure()));
	        TaskFValue taskFValue = KernelManager.getFilter().getTaskFValue(MY_FILTER);
	       taskFValue.set(FieldMap.HUSER_NAME.getFilterKey(), user.getId());
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
