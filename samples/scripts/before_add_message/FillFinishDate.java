package scripts.before_add_message;

import java.util.Calendar;
import java.util.HashMap;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;


public class FillFinishDate implements OperationTrigger{
    private static String UDFID = "ff80818131e11fca0131e141a760013e";
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
       SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(message.getSecure(), UDFID);
       if (message.getUdfValue(udf.getCaption())==null || message.getUdfValue(udf.getCaption()).length()==0){
       HashMap<String, SecuredUDFValueBean> m = message.getTask().getUDFValues();
       SecuredUDFValueBean finish = m.get(UDFID);
           if (finish==null || finish.getValue()==null){
               message.setUdfValue(udf.getCaption(), message.getSecure().getUser().getDateFormatter().parse(Calendar.getInstance()));
           }
       }
        return message;

    }
}
