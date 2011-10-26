package scripts.task_custom_field_value;


import com.trackstudio.secured.*;
import com.trackstudio.exception.GranException;

import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import scripts.CommonScrum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Calendar;
import javax.servlet.ServletContext;

public class ScrumSprintEstimation extends CommonScrum implements TaskUDFValueScript {

    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {
        try {
            if (securedTaskBean.getCategoryId().equals(SCRUM_SRINT_CATEGORY)){
            String teamMemberId = SCRUM_TEAM_MEMBER_GROUP;
            if (teamMemberId == null) teamMemberId = "";
            String cmessageId = SCRUM_ITEM_OPERATION_ESTIMATE;
            if (cmessageId == null) cmessageId = "";


            Long budget = securedTaskBean.getBudget();
            ServletContext context = SessionContext.getServletConfig().getServletContext();
            Long calculatedBudget = 0L;
            ArrayList<SecuredUserBean> userList = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(securedTaskBean.getSecure(), securedTaskBean.getId());

            Collections.sort(userList);
            Long totalFinished = 0L;
            Long totalUnfinished = 0L;
            HashMap<SecuredUserBean, Long> team = new HashMap<SecuredUserBean, Long>();
            float weeks = 0f;
            float weeksFromNow = 0f;
            long teamwork = 0L;
            String customField = SCRUM_TEAM_WORKHOURS;
            StringBuffer taskTable = new StringBuffer();
            Calendar now = Calendar.getInstance();
            if (securedTaskBean.getDeadline() != null) {
                Calendar update = securedTaskBean.getUpdatedate();


                if (customField != null) {
                    Calendar deadline = securedTaskBean.getDeadline();


                    weeks = (deadline.getTimeInMillis() - update.getTimeInMillis()) / 168000f;
                    weeksFromNow = (deadline.getTimeInMillis() - now.getTimeInMillis()) / 168000f;

                }
            }

            for (SecuredUserBean us2 : userList) {
                for (SecuredPrstatusBean bean : AdapterManager.getInstance().getSecuredAclAdapterManager().getAllowedPrstatusList(securedTaskBean.getSecure(), securedTaskBean.getId(), us2.getId())) {
                    if (bean.getId().equals(teamMemberId)) team.put(us2, -1L);
                }
            }
            boolean odd = false;
            for (SecuredTaskBean task : securedTaskBean.getChildren()) {

                taskTable.append("<tr ");
                if (odd) taskTable.append("class=\"line0\">");
                else taskTable.append("class=\"line1\">");

                odd = !odd;
                taskTable.append("<td>");
                taskTable.append("<a class=\"internal\" href=\"");

                taskTable.append(context.getContextPath());
                taskTable.append("/task/");
                taskTable.append(task.getNumber());
                taskTable.append("\">");
                
                taskTable.append("<img class=\"state\" border=\"0\"")
                        .append(" style=\"background-color: ")
                        .append(task.getStatus().getColor())
                        .append("; margin-right: 5px;\"")
                        .append(" src=\"")
                 .append( context.getContextPath())
                 .append("/cssimages/");
                if (task.getStatus().isFinish())
                        taskTable.append("finishstate.gif");
                else taskTable.append("state.gif");
                        taskTable.append("\">");

                taskTable.append(task.getName());
                taskTable.append("</a>");
                taskTable.append("</td>");

                Long aLong = task.getBudget();
                calculatedBudget += aLong;
                SecuredUserBean handler = task.getHandlerUser();
                taskTable.append("<td>");


                if (handler != null) {
                    taskTable.append("<span class=\"user\"");
                    if (handler.getId().equals(task.getSecure().getUserId())) {
                        taskTable.append(" id='loggedUser'");
                    }
                    taskTable.append(">");
                    taskTable.append("<img src=\"").append( context.getContextPath()).append( "/cssimages/arw.usr.a.gif\">&nbsp;");
                    taskTable.append(handler.getName());

                    taskTable.append("</span>");
                    if (team.get(handler) != null) {
                        Long b = team.get(handler);
                        if (b>=0)
                        team.put(handler, b + aLong);
                        else team.put(handler, aLong);
                    }
                }
                taskTable.append("</td>");
                taskTable.append("<td>");
                taskTable.append(task.getBudgetAsString());
                taskTable.append("</td>");
                boolean thisTaskIsFinished = task.getClosedate() != null;
                if (thisTaskIsFinished){
                    totalFinished += task.getBudget();
                } else {
                  totalUnfinished += task.getBudget();
                }
                if (!securedTaskBean.getStatus().isStart()) {
                    if (task.getActualBudget() != null) {
                        taskTable.append("<td>");
                        if (task.getBudget()>=task.getActualBudget()) {

                            taskTable.append("<SPAN style=\"color: green\">");
                            taskTable.append(task.getActualBudgetAsString());
                            taskTable.append("</SPAN>");

                        } else {

                            taskTable.append("<SPAN style=\"color: red\">");
                            taskTable.append(task.getActualBudgetAsString());
                            taskTable.append("</SPAN>");
                        }
                        taskTable.append("</td>");
                    } else {
                        taskTable.append("<td>");
                        taskTable.append("</td>");
                    }
                }
                if (!securedTaskBean.getStatusId().equals(SCRUM_SPRINT_STATE_RUN) && !securedTaskBean.getStatus().isFinish()) {
                    taskTable.append("<td>");

                    for (SecuredMessageBean m : task.getMessages()) {
                        if (m.getMstatus().getId().equals(cmessageId)) {
                            if (m.getBudget() != null && team.containsKey(m.getSubmitter())) {
                                taskTable.append("<span class=\"user\"");
                                if (m.getSubmitter().getId().equals(task.getSecure().getUserId())) {
                                    taskTable.append(" id='loggedUser'");
                                }
                                taskTable.append(">");
                                taskTable.append("<img src=\"").append(context.getContextPath()).append("/cssimages/arw.usr.a.gif\">&nbsp;");
                                taskTable.append(m.getSubmitter().getName());

                                taskTable.append("</span>");
                                taskTable.append(":&nbsp;");
                                taskTable.append(m.getBudgetAsString());
                                taskTable.append("<br>");
                            }
                        }

                    }
                    taskTable.append("</td>");
                }
                taskTable.append("</tr>");
            }
            // now we have list of team members
            /*

            */
            StringBuffer buf = new StringBuffer();
            StringBuffer buf2 = new StringBuffer();

        /*    if (budget > 0) { */

                if (!team.isEmpty()) {
                    buf2.append("<table width=\"100%\"><tr class=\"wide\">");
                    buf2.append("<th width=\"30%\">");
                    buf2.append("Участники");
                    buf2.append("</th>");
                    buf2.append("<th width=\"30%\">");
                    buf2.append("Занятость");
                    buf2.append("</th>");
                    buf2.append("<th width=\"20%\">");
                    buf2.append("Полная");
                    buf2.append("</th>");
                    buf2.append("</tr>");
                    odd = false;

                    for (SecuredUserBean u : team.keySet()) {
                        if (u.canManage()){
                        buf2.append("<tr ");
                        if (odd) buf2.append("class=\"line0\">");
                        else buf2.append("class=\"line1\">");
                        odd = !odd;
                        buf2.append("<td>");
                        buf2.append("<span class=\"user\"");
                        if (u.getId().equals(securedTaskBean.getSecure().getUserId())) {
                            buf2.append(" id='loggedUser'");
                        }
                        buf2.append(">");
                        buf2.append("<img src=\"").append(context.getContextPath()).append( "/cssimages/arw.usr.a.gif\">&nbsp;");
                        buf2.append(u.getName());

                        buf2.append("</span>");
                        buf2.append("</td>");

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
                        teamwork += time2;
                        Long h = team.get(u);
                        long h2 = (long) (time2 * weeks);
                        HourFormatter hf2 = new HourFormatter(h2, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                        if (h==null || h<0){
                            buf2.append("<td>");
                            buf2.append("<span style=\"color: red\">не занят</span>");
                            buf2.append("</td><td>");
                            buf2.append(hf2.getString());
                            buf2.append("</td>");
                        } else {
                            buf2.append("<td>");
                            if (h>0){
                            HourFormatter hf = new HourFormatter(h, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                            if (h>h2){
                            buf2.append("<span style=\"color: red\">");
                            buf2.append(hf.getString());
                            buf2.append("</span>");
                            } else buf2.append(hf.getString()); 

                            }
                            else buf2.append("уже освободился");
                            buf2.append("</td><td>");
                            buf2.append(hf2.getString());
                            buf2.append("</td>");
                        }
                        buf2.append("</tr>");
                    }
                    }


                }

                buf2.append("</table>");
           /* } if budget */

            buf.append("<table width=\"100%\">");
            if (securedTaskBean.getStatus().isStart()) {
                buf.append("<tr class=\"wide\">");
                buf.append("<th></th>");
                buf.append("<th colspan=2 width=\"60%\" style=\"text-align: center\">Спринт планируется</th>");
                buf.append("<th><nobr>завершить до: ");
                buf.append(securedTaskBean.getDeadlineAsString());
                buf.append("</nobr></th>");
                buf.append("</tr>");

                buf.append("<tr>");
                buf.append("<td colspan=2>");
                buf.append("По плану: ");
                HourFormatter budgetHF = new HourFormatter(budget, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                buf.append(budgetHF.getString());
                buf.append("</td>");
                buf.append("<td colspan=2>");
                if (securedTaskBean.getSubmitterId().equals(securedTaskBean.getSecure().getUserId())){
                buf.append("Осталось времени: ");
                HourFormatter budgetHF2 = new HourFormatter((long) (teamwork * weeks), securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                buf.append(budgetHF2.getString());
                }
                buf.append("</td>");
                buf.append("</tr>");

                buf.append("<tr>");
                buf.append("<td colspan=2>");
                buf.append("Уже запланировано: ");
                HourFormatter budgetHF3 = new HourFormatter(calculatedBudget, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                buf.append(budgetHF3.getString());
                buf.append("</td>");

                if (budget >= calculatedBudget) {
                    buf.append("<td colspan=2>");
                    buf.append("Можно запланировать: ");
                    HourFormatter budgetHF4 = new HourFormatter(budget - calculatedBudget, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                    buf.append(budgetHF4.getString());
                } else {
                    buf.append("<td colspan=2 style=\"color:red\">");
                    buf.append("Нужно освободить: ");
                    HourFormatter budgetHF5 = new HourFormatter(calculatedBudget - budget, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                    buf.append(budgetHF5.getString());
                }
                buf.append("</td>");
                buf.append("</tr>");

            } else if (securedTaskBean.getStatusId().equals(SCRUM_SPRINT_STATE_RUN)) {
                long runAt = 0L;
                buf.append("<tr class=\"wide\">");
                buf.append("<th><nobr>");
                for (SecuredMessageBean m : securedTaskBean.getMessages()) {
                    if (m.getMstatusId().equals(SCRUM_SPRINT_OPERATION_RUN)) {
                        runAt = m.getTime().getTimeInMillis();
                        buf.append(m.getTimeAsString());
                        break;
                    }

                }

                buf.append("</nobr></th>");
                buf.append("<th colspan=2 width=\"60%\" style=\"text-align: center\">Спринт запущен</th>");
                buf.append("<th><nobr>завершить до: ");
                buf.append(securedTaskBean.getDeadlineAsString());
                buf.append("</nobr></th>");
                buf.append("</tr>");

                buf.append("<tr>");
                buf.append("<td colspan=2>");
                buf.append("Длительность: ");
                HourFormatter budgetHF = new HourFormatter(budget, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                buf.append(budgetHF.getString());
                buf.append("</td>");
                buf.append("<td colspan=2>");
                if (securedTaskBean.getSubmitterId().equals(securedTaskBean.getSecure().getUserId())){
                buf.append("Резерв: ");
                HourFormatter budgetHF2 = new HourFormatter((long) (teamwork * weeksFromNow), securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                buf.append(budgetHF2.getString());
                }
                buf.append("</td>");
                buf.append("</tr>");


                buf.append("<tr>");
                buf.append("<td colspan=2>");
                buf.append("Осталось делать: ");
                HourFormatter budgetHF3 = new HourFormatter(totalUnfinished, securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                buf.append(budgetHF3.getString());
                buf.append("</td>");

                
                long allTime = (securedTaskBean.getDeadline().getTimeInMillis() - runAt);
                long nowTime = securedTaskBean.getDeadline().getTimeInMillis() - now.getTimeInMillis();//168000f;
                float mustComplete = (1f-(float)nowTime / (float)allTime) * budget;


                if (totalFinished >= mustComplete) {
                    buf.append("<td colspan=2>");
                    buf.append("Опережение: ");
                    HourFormatter budgetHF4 = new HourFormatter((long) (mustComplete), securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                    buf.append(budgetHF4.getString());
                } else {
                    buf.append("<td colspan=2 style=\"color:red\">");
                    buf.append("Отставание: ");
                    HourFormatter budgetHF5 = new HourFormatter((long) (mustComplete - totalFinished), securedTaskBean.getBudgetFormat(), securedTaskBean.getSecure().getLocale());
                    buf.append(budgetHF5.getString());
                }
                buf.append("</td>");
                buf.append("</tr>");
            }
            buf.append("</table>");
            buf.append("<br>");

            buf.append(buf2);

            // подзадачи. Бэклог
            buf.append("<br>");
            buf.append("<table width=\"100%\"><tr class=\"wide\">");
            buf.append("<th>");
            buf.append("Задача");
            buf.append("</th>");
            buf.append("<th>");
            buf.append("Ответственный");
            buf.append("</th>");
            buf.append("<th>");
            buf.append("Бюджет");
            buf.append("</th>");
            if (!securedTaskBean.getStatus().isStart()) {
                buf.append("<th>");
                buf.append("Израсходовано");
                buf.append("</th>");
            }
            if (!securedTaskBean.getStatusId().equals(SCRUM_SPRINT_STATE_RUN) && !securedTaskBean.getStatus().isFinish()) {
                buf.append("<th>");
                buf.append("Оценки");
                buf.append("</th>");
            }
            buf.append("</tr>");
            buf.append(taskTable);
            buf.append("</table>");


            return buf.toString();
            }
            else return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new GranException(e);
        }
    }

}
