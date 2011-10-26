package scripts.task_custom_field_value;


import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;

import com.trackstudio.secured.SecuredUDFValueBean;


import java.util.Calendar;
import java.util.HashMap;

public class Overdue implements TaskUDFValueScript {

    public Object calculate(SecuredTaskBean task) throws GranException {
        HashMap<String, SecuredUDFValueBean> ul = task.getUDFValues();
        SecuredUDFValueBean udf = ul.get("ff80818131e5e6600131e621bd3b00bd");

        Calendar date = null;
        if (udf != null) {
            Object v = udf.getValue();
            if (v != null) {
                date = (Calendar) v;
            }
        }
        if (date == null) {
            if (task.getClosedate() != null) date = task.getClosedate();
            else date = null;
        }
        if (task.getDeadline()!=null && task.getDeadline().before(date)) return 1;
        else return 0;


    }
}
