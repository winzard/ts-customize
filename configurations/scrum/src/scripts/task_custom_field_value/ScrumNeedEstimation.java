package scripts.task_custom_field_value;


import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.exception.GranException;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.external.TaskUDFValueScript;

import java.util.*;

import scripts.CommonScrum;


public class ScrumNeedEstimation extends CommonScrum implements TaskUDFValueScript {

    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {

             ArrayList<SecuredUserBean> userList = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(securedTaskBean.getSecure(), securedTaskBean.getId());
             Collections.sort(userList);

             
             String cmessageId = SCRUM_ITEM_OPERATION_ESTIMATE;
             if (cmessageId==null) cmessageId="";
              // now we have list of team members
        boolean alreadyDone = false;
             for (SecuredMessageBean m :securedTaskBean.getMessages()){
                 if (m.getMstatus().getId().equals(cmessageId)){
                     if (m.getBudget()!=null && m.getSubmitterId().equals(securedTaskBean.getSecure().getUserId())){
                         alreadyDone = true;
                         break;
                     }
                 }
             }
             StringBuffer buf = new StringBuffer();
             if (alreadyDone)
             buf.append("нет");
        else
             buf.append("да");

             return buf.toString();
         }


}
