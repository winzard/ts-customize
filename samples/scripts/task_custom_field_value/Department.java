package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;

public class Department implements TaskUDFValueScript {

    public Object calculate(SecuredTaskBean task) throws GranException {
        if (task.getHandlerUserId()!=null)
            return task.getHandlerUser().getCompany();
        else return "";
    }
}
