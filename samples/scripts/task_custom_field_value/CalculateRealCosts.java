package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;

import java.util.HashMap;


public class CalculateRealCosts implements TaskUDFValueScript {
    private static String UDFID = "ff80818131e11fca0131e13cf7d90108";

    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {
       Double result = 0d;
       for (SecuredTaskBean task : securedTaskBean.getChildren()) {
             HashMap<String, SecuredUDFValueBean> m = task.getUDFValues();
             SecuredUDFValueBean costs = m.get(UDFID);
           if (costs!=null){
               Object v = costs.getValue();
               try{
               if (v!=null) result+=(Double)v;
               } catch (NumberFormatException ne){};
           }
       }
        return result;
    }

}
