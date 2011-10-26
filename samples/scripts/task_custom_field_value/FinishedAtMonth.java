package scripts.task_custom_field_value;


import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;

public class FinishedAtMonth implements TaskUDFValueScript {

    public Object calculate(SecuredTaskBean task) throws GranException {
        HashMap<String, SecuredUDFValueBean> ul = task.getUDFValues();
        SecuredUDFValueBean udf = ul.get("ff80818131e5e6600131e621bd3b00bd");
        DateFormatSymbols symbols = new DateFormatSymbols(task.getSecure().getUser().getDateFormatter().getLocale());
        Calendar date = null;
        if (udf != null) {
            Object v = udf.getValue();
            if (v != null) {
                date = (Calendar) v;
            }
        }
        if (date == null) {
            if (task.getClosedate() != null) date = task.getClosedate();
            else date = task.getUpdatedate();
        }
        String s = symbols.getMonths()[date.get(Calendar.MONTH)];

        return s;
    }
}
