package scripts.task_custom_field_value;

import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.*;
import com.trackstudio.exception.GranException;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.tools.formatter.HourFormatter;
import java.util.*;
import scripts.CommonScrum;



public class ScrumItemEstimation extends CommonScrum implements TaskUDFValueScript{

    private HashMap<SecuredUserBean, String> getTeamBussiness(SecuredTaskBean securedTaskBean, Set<SecuredUserBean> userList) throws GranException{
        HashMap<SecuredUserBean,  String> map = new HashMap<SecuredUserBean, String>();
            if (securedTaskBean.getCategoryId().equals(SCRUM_SRINT_CATEGORY)){
            Calendar now = Calendar.getInstance();
            HashMap<SecuredUserBean, Long> team = new HashMap<SecuredUserBean, Long>();
            float weeks = 0f;

            String customField = SCRUM_TEAM_WORKHOURS;
            if (securedTaskBean.getDeadline() != null) {
                if (customField != null) {
                    Calendar deadline = securedTaskBean.getDeadline();
                    weeks = (deadline.getTimeInMillis() - now.getTimeInMillis()) / 168000f;

                }
            }

            for (SecuredUserBean us2 : userList) {
                    team.put(us2, 0L);
            }

            for (SecuredTaskBean task : securedTaskBean.getChildren()) {
                Long aLong = task.getBudget();
                SecuredUserBean handler = task.getHandlerUser();
                if (handler != null) {
                    if (team.get(handler) != null) {
                        Long b = team.get(handler);
                        if (b>=0)
                        team.put(handler, b + aLong);
                        else team.put(handler, aLong);
                    }
                }
                
           }
            if (!team.isEmpty()) {
                    for (SecuredUserBean u : team.keySet()) {
                        SecuredUDFValueBean udfValueBean = u.getUDFValues().get(customField);
                        int hours = 0;
                        if (udfValueBean != null) {
                            Object v = udfValueBean.getValue();
                            if (v != null) {
                                hours = (Integer.parseInt(v.toString()));
                           }
                        } else {
                            hours = (Integer.parseInt(customField));
                        }
                        long time2 = (long) hours;

                        Long h = team.get(u);
                        long h2 = (long) (time2 * weeks);
                        HourFormatter hf2 = new HourFormatter(h2, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                        HourFormatter hf = new HourFormatter(h, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                            if (h2>h)
                            map.put(u,hf.getString()+" ("+hf2.getString()+")");
                        else map.put(u,"<span style=\"color: red\">"+hf.getString()+"</span> ("+hf2.getString()+")");

                    }
                }
            }
        return map;
    }

    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {
             if (securedTaskBean.getStatus().isStart() || securedTaskBean.getStatusId().equals(SCRUM_ITEM_STATE_READY)){
             ArrayList<SecuredUserBean> userList = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(securedTaskBean.getSecure(), securedTaskBean.getId());
             Collections.sort(userList);
             HashMap<SecuredUserBean, SecuredMessageBean> team = new HashMap<SecuredUserBean, SecuredMessageBean>();
        
             String teamMemberId = SCRUM_TEAM_MEMBER_GROUP;
             if (teamMemberId==null) teamMemberId="";
             String cmessageId = SCRUM_ITEM_OPERATION_ESTIMATE;
             if (cmessageId==null) cmessageId="";
                  for (SecuredUserBean us2 : userList) {
                      for (SecuredPrstatusBean bean : AdapterManager.getInstance().getSecuredAclAdapterManager().getAllowedPrstatusList(securedTaskBean.getSecure(), securedTaskBean.getId(), us2.getId())){
                          if (bean.getId().equals(teamMemberId)) team.put(us2, null);
                      }
                  }
              // now we have list of team members

             for (SecuredMessageBean m :securedTaskBean.getMessages()){
                 if (m.getMstatus().getId().equals(cmessageId)){
                     if (m.getBudget()!=null && team.containsKey(m.getSubmitter())){
                         team.put(m.getSubmitter(), m);
                     }
                 }
             }
             StringBuffer buf = new StringBuffer();
             if (!team.isEmpty()){
             buf.append("<table><tr>");
             buf.append("<caption>");
             buf.append("Список участников");
             buf.append("</caption>");
             buf.append("<th>");
             buf.append("Участники");
             buf.append("</th>");
             buf.append("<th>");
             buf.append("Оценка");
             buf.append("</th>");
             HashMap<SecuredUserBean, String> m = getTeamBussiness(securedTaskBean.getParent(), team.keySet());
                 if (!m.isEmpty()){
             buf.append("<th>");
             buf.append("Занятость");
             buf.append("</th>");
                 }
             buf.append("</tr>");
             Long budget = 0L;
             byte counter =0;
             boolean iaminteam = team.containsKey(securedTaskBean.getSecure().getUser());
             for (SecuredUserBean u: team.keySet()){
                 if (!iaminteam || SCRUM_TEAM_SEE_OTHERS.equals("yes") || u.getId().equals(securedTaskBean.getSecure().getUserId())){
                 buf.append("<tr><td>");
                 buf.append("<span class=\"user\"");
                 if (u.getId().equals(securedTaskBean.getSecure().getUserId())){
                     buf.append(" id='loggedUser'");
                 }
                 buf.append(">");
                 buf.append(u.getName());

                 buf.append("</span>");
                 buf.append("</td>");
                 SecuredMessageBean h = team.get(u);
                 if (h!=null){
                     buf.append("<td>");

                 buf.append(team.get(u).getBudgetAsString());
                 budget+=team.get(u).getBudget();
                     counter++;
                     buf.append("</td>");
                 }
                 else {
                     buf.append("<td>");
                     buf.append("<span style=\"color: red\"> не отметился</span>");
                     buf.append("</td>");


             }
                     if (!m.isEmpty()){
             buf.append("<td>");
             buf.append(m.get(u));
             buf.append("</td>");
                 }
                     buf.append("</tr>");
             }
             }
             if (counter>0){
              HourFormatter hf = new HourFormatter(budget/counter, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
             buf.append("<tr><th>");
             buf.append("Средняя оценка");
                 buf.append("</th><th>");
             buf.append(hf.getString());
                 buf.append("</th>");
                 if (!m.isEmpty()){
                     buf.append("<th></th>");
                 }

             buf.append("</tr>");
             }
             buf.append("</table>");

             }
             return buf.toString();
             } else return "";
         }

   
}
