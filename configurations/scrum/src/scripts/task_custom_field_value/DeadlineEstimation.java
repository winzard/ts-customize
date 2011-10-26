package scripts.task_custom_field_value;

import scripts.CommonScrum;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.exception.GranException;
import com.trackstudio.app.adapter.AdapterManager;

import java.util.ArrayList;
import java.util.Calendar;

public class DeadlineEstimation extends CommonScrum implements TaskUDFValueScript{
    /** Вычисляем ресурсы до дедлайна
     * У нас есть команда и есть дедлайн. Для команды мы шерстим юзерское поле с именем, зашитым в scrum.team.workhours
     * Там либо кастом поле, в котором в часах написано количество часов, которые юзер в неделю может/будет отрабатывать,
     * либо непосредственно часы в неделю, если нет поюзерной дифференциации
     * @param securedTaskBean
     * @return
     * @throws GranException
     */
    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {

        if (securedTaskBean.getDeadline()!=null){
        String customField = SCRUM_TEAM_WORKHOURS;
        if (customField!=null){
           String teamMemberId = SCRUM_TEAM_MEMBER_GROUP;
             if (teamMemberId==null) teamMemberId="";

             ArrayList<SecuredUserBean> userList = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(securedTaskBean.getSecure(), securedTaskBean.getId());
            int common = 0;

                  for (SecuredUserBean us2 : userList) {
                      for (SecuredPrstatusBean bean : AdapterManager.getInstance().getSecuredAclAdapterManager().getAllowedPrstatusList(securedTaskBean.getSecure(), securedTaskBean.getId(), us2.getId())){
                          if (bean.getId().equals(teamMemberId)) {
                              
                          SecuredUDFValueBean udfValueBean = us2.getUDFValues().get(customField);
                     int hours = 0;
                     if (udfValueBean!=null) {
                         Object v = udfValueBean.getValue();
                         if (v!=null){
                             hours = (Integer.parseInt(v.toString()));
                             
                         }

                     } else {
                         hours=(Integer.parseInt(customField));
                     }
                                 common+=hours;
                      }

                  }
                  }
            Calendar deadline = securedTaskBean.getDeadline();
            Calendar update = securedTaskBean.getUpdatedate();
            long diff = deadline.getTimeInMillis()-update.getTimeInMillis();
            float workweeks = (diff/604800000f);
            
            return (int)(common*workweeks);
        }
        }
        return 0;

    }
}
