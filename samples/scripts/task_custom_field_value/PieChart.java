package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Распределение состояний задач в проекте на текущий момент времени
 * @author winzard
 *
 */
public class PieChart  implements TaskUDFValueScript {

    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {

                ArrayList<SecuredTaskBean> children = securedTaskBean.getChildren();
                if (children==null || children.isEmpty()) return null;
                HashMap<SecuredStatusBean, Integer> states = new HashMap<SecuredStatusBean, Integer>();
                for (SecuredTaskBean task : children) {


                            if (states.containsKey(task.getStatus())){
                                Integer amo=states.get(task.getStatus());
                                states.put(task.getStatus(), amo+1);
                            }
                            else states.put(task.getStatus(), 1);
                        }


                StringBuffer s = new StringBuffer();
                s.append("<div id=\"piechart_div").append(securedTaskBean.getId()).append("\"></div>\n");
                s.append("<script type=\"text/javascript\">\n");
                s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
                s.append("google.setOnLoadCallback(pieChart").append(securedTaskBean.getId()).append(");\n");
                s.append("function pieChart").append(securedTaskBean.getId()).append("() {\n");
                s.append("var data = new google.visualization.DataTable();\n");
        		s.append("data.addColumn(\'string\', \'Состояния\');\n");
        		s.append("data.addColumn(\'number\', \'Количество задач\');\n");
        		for (SecuredStatusBean u :states.keySet()){
                	s.append("data.addRow([\'").append(u.getName()).append("\', ").append(states.get(u)).append("]);\n");
                }
                s.append("var options = {\'title\':\'").append(securedTaskBean.getName()).append("\',\n");
        		s.append("\'backgroundColor\': \'transparent\',\n");
        		s.append("\'width\':200,\n");
        		s.append("\'height\':200,\n");
        		s.append("\'colors\': [");
         		for (SecuredStatusBean state: states.keySet())
         	        s.append("\'").append(state.getColor()).append("\', ");
         		s.append("\'black\'],\n");
        		s.append("\'legend\': \'none\'};\n");
        		

        		s.append("		      var chart = new google.visualization.PieChart(document.getElementById(\'piechart_div").append(securedTaskBean.getId()).append("\'));\n");
        		//s.append(" data.sort({column: 0});");
        		s.append("chart.draw(data, options);\n");
        		s.append("}\n");
        		s.append("</script>\n");
                return s.toString();
    }
}
