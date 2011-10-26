package scripts.before_add_message;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;

import java.util.Calendar;
import java.util.HashMap;


public class FillStartDate implements OperationTrigger{
    private static String UDFID = "ff80818131e11fca0131e1409b650119";
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
       SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(message.getSecure(), UDFID);
       if (message.getUdfValue(udf.getCaption())==null ||message.getUdfValue(udf.getCaption()).length()==0){
       HashMap<String, SecuredUDFValueBean> m = message.getTask().getUDFValues();
       SecuredUDFValueBean finish = m.get(UDFID);
           if (finish==null || finish.getValue()==null){
               message.setUdfValue(udf.getCaption(), message.getSecure().getUser().getDateFormatter().parse(Calendar.getInstance()));
           }
       }
        return message;

    }
}
