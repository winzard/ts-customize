package scripts.task_custom_field_value;


import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;

import java.util.ArrayList;
import java.util.List;

public class Subtasks  implements TaskUDFValueScript{
    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {
        List<String> taskIds = new ArrayList<String>();
        for (SecuredTaskBean task: securedTaskBean.getChildren()){
            taskIds.add("#"+task.getNumber());
        }
        return taskIds;
    }

}
