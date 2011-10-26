package scripts.before_edit_task;


import java.util.ArrayList;
import java.util.List;

import scripts.CommonSubscriber;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUserBean;


public class SubscribeFellows extends CommonSubscriber implements TaskTrigger {

    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean securedTaskTriggerBean) throws GranException {
        ArrayList<SecuredUserBean> users = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(securedTaskTriggerBean.getSecure(), securedTaskTriggerBean.getId());
        List<String> toRemove = new ArrayList<String>();
        for (SecuredUserBean f : users) {
            toRemove.add(f.getId());
        }
        unsubscribe(securedTaskTriggerBean.getId(), toRemove);
        //SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(securedTaskTriggerBean.getSecure(), FELLOWS_UDFID);

            String fellowsUDF = securedTaskTriggerBean.getUdfValue(FELLOWS_UDF);
            if (fellowsUDF != null) {
                String[] value = fellowsUDF.split(";");
                if (value!=null && value.length>0)
                for (String login : value) {
                    if (login.length()>0){
                    SecuredUserBean f = AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(securedTaskTriggerBean.getSecure(), login.substring(1)); //skip @
                    if (f != null) {
                        subscribe(f, securedTaskTriggerBean.getId());
                    }
                    }
                }


            }

        return securedTaskTriggerBean;
    }
}
