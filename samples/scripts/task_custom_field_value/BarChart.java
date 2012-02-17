package scripts.task_custom_field_value;


import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
/**
 * Диаграмма распределения потраченных часов по исполнителям
 * @author winzard
 *
 */
public class BarChart  implements TaskUDFValueScript {
    public HashMap<SecuredUserBean, Long> getParticipants(SecuredTaskBean securedTaskBean) throws GranException {
        SessionContext sc = securedTaskBean.getSecure();
        ArrayList<SecuredUserBean> userList = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(sc, securedTaskBean.getId());
        Collections.sort(userList);
        HashMap<SecuredUserBean, Long> ret = new HashMap<SecuredUserBean, Long>();
        for (SecuredUserBean u : userList) {
            ArrayList<SecuredPrstatusBean> statuses = AdapterManager.getInstance().getSecuredAclAdapterManager().getAllowedPrstatusList(sc, securedTaskBean.getId(), u.getId());
            for (SecuredPrstatusBean b : statuses){
                if (sc.canActionPrstatus(Action.editTaskActualBudget, b.getId())){
            ret.put(u, 0L);
                    break;
                }
            }
            }

        return ret;

    }
    
    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {

                ArrayList<SecuredTaskBean> children = securedTaskBean.getChildren();
                if (children==null || children.isEmpty()) return null;
                HashMap<SecuredUserBean, Long> participants = getParticipants(securedTaskBean);
                for (SecuredTaskBean task : children) {

                    for (SecuredMessageBean msg : task.getMessages()) {
                        if (msg.getHrs()!=null) {
                            if (participants.containsKey(msg.getSubmitter())){
                                Long spentTime=participants.get(msg.getSubmitter());
                                participants.put(msg.getSubmitter(), spentTime+msg.getHrs());
                            }
                        }
                    }
                }
                StringBuffer s = new StringBuffer();
                
                s.append("<div id=\"barchart_div").append(securedTaskBean.getId()).append("\"></div>\n");
                s.append("<script type=\"text/javascript\">\n");
                s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
                s.append("google.setOnLoadCallback(drawChart").append(securedTaskBean.getId()).append(");\n");
                s.append("function drawChart").append(securedTaskBean.getId()).append("() {\n");
                s.append("var data = new google.visualization.DataTable();\n");
        		s.append("data.addColumn(\'string\', \'Участники\');\n");
        		s.append("data.addColumn(\'number\', \'Рабочие часы\');\n");
                for (SecuredUserBean u :participants.keySet()){
                	s.append("data.addRow([\'").append(u.getName()).append("\', ").append((float)participants.get(u)/3600f).append("]);\n");
                }
                s.append("var options = {\'title\':\'Рабочие часы\',\n");
        		s.append("\'backgroundColor\': \'transparent\',\n");
        		s.append("\'legend\': \'none\'};\n");
        		

        		s.append("		      var chart = new google.visualization.BarChart(document.getElementById(\'barchart_div").append(securedTaskBean.getId()).append("\'));\n");
        		s.append(" data.sort({column: 0});");
        		s.append("chart.draw(data, options);\n");
        		s.append("}\n");
        		s.append("</script>\n");
        		
                return s.toString();
    }
}
