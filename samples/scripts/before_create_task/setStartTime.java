package scripts.before_create_task;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;


import java.util.Calendar;

public class setStartTime  implements TaskTrigger {
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {

        if (task.getUdfValue("start time")==null || task.getUdfValue("start time").length()==0){
            task.setUdfValue("start time", task.getSecure().getUser().getDateFormatter().parse(Calendar.getInstance()));
        }
        return task;
    }
}
