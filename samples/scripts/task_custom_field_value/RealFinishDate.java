package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;

import java.util.Calendar;
import java.util.HashMap;


public class RealFinishDate implements TaskUDFValueScript {
    private static String UDFID = "ff80818131e11fca0131e141a760013e";


    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {
       Calendar result = securedTaskBean.getSubmitdate();
       for (SecuredTaskBean task : securedTaskBean.getChildren()) {
             HashMap<String, SecuredUDFValueBean> m = task.getUDFValues();
             SecuredUDFValueBean finish = m.get(UDFID);
           if (finish!=null){
               Object v = finish.getValue();

               if (v!=null) {
                   Calendar c = (Calendar)v;
                   if (c.after(result)) result = c;
               }

           }
       }
        return result;
    }

}
