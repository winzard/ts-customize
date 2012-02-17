package scripts.task_custom_field_value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import scripts.task_custom_field_value.AreaChart.DayPeriods;
import scripts.task_custom_field_value.AreaChart.MonthPeriods;
import scripts.task_custom_field_value.AreaChart.Periods;
import scripts.task_custom_field_value.AreaChart.WeekPeriods;

import com.trackstudio.app.filter.customizer.BudgetCustomizer;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTransitionBean;
import com.trackstudio.tools.formatter.HourFormatter;
/**
 * Графическое представление количества оставшихся (незакрытых) задач и оставшегося до deadline проекта времени.<br>
 * Используются следующие данные:<br>
 * Дата закрытия задачи<br>
 * Бюджет задачи (при его отсутствии - потраченное время) и бюджет проекта<br>
 * Дедлайн проекта<br>
 * Длительность спринта - время от создания спринта (проекта) до дедлайна<br>
 * При планировании учитываются выходные дни (длительность рабочей недели и рабочего дня задается в стандартных свойствах TrackStudio) 
 */
public class BurndownChart extends ActivityChart implements TaskUDFValueScript {
	
	@Override
	public Object calculate(SecuredTaskBean securedTaskBean)
			throws GranException {
        HashMap<SecuredStatusBean, Integer[]> chartTable = new HashMap<SecuredStatusBean, Integer[]>();
        Calendar endAt = securedTaskBean.getDeadline();
        Long budget = securedTaskBean.getBudget();
		ArrayList<SecuredTaskBean> children = securedTaskBean.getChildren();
		
		if (budget!=null && endAt!=null && children!=null && !children.isEmpty()){
		Calendar startAt = securedTaskBean.getSubmitdate();
		
        
        Periods periods = new DayPeriods();
        int totalPeriods = periods.since(endAt, startAt);
        Long[] real = new Long[totalPeriods+1];
        Long[] ideal = new Long[totalPeriods+1];
        Arrays.fill(real, budget);
        Arrays.fill(ideal, budget);
        Calendar iterate = (Calendar)startAt.clone();
        int totalDays = 0;
        for( int i=1; i<= totalPeriods; i++){
        
            if (iterate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && iterate.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
               totalDays++;
            }
            iterate.add(Calendar.HOUR, 24);
        }
        Long workPerDay = budget/totalDays;
        iterate = (Calendar)startAt.clone();
        Long left = budget; 
        for( int i=1; i<= totalPeriods; i++){
            
                if (iterate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && iterate.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                	left -= workPerDay;
                }
                ideal[i] = left;
                iterate.add(Calendar.HOUR, 24);
            }
            
        for (SecuredTaskBean task : children) {
            if (task.getClosedate()!=null){
            	int period = periods.since( task.getClosedate(), startAt);
            	Long b = task.getBudget();
            	if (b==null || b==0)
            		b = task.getAbudget();
            	if (b!=null){
            		for( int i=period; i<= totalPeriods; i++){
            			real[i] -= b;
                    }	
            	}
            }
        }
        
        StringBuffer s = new StringBuffer();
        s.append("<div id=\"burndownchart_div").append(securedTaskBean.getId()).append("\"></div>\n");
        s.append("<script type=\"text/javascript\">\n");
        s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
        s.append("google.setOnLoadCallback(drawBurndownChart").append(securedTaskBean.getId()).append(");\n");
        s.append("function drawBurndownChart").append(securedTaskBean.getId()).append("() {\n");
        s.append("var data = new google.visualization.DataTable();\n");
        s.append("data.addColumn(\'date\', \'").append("Период").append("\');\n");
        s.append("data.addColumn(\'number\', \'").append("Идеально").append("\');\n");
        s.append("data.addColumn(\'number\', \'").append("Реально").append("\');\n");
        
        Calendar flow = (Calendar)startAt.clone();
         for (int i =0 ; i<= totalPeriods; i++){
         	flow.add(periods.getShift(), 1);      
            s.append("data.addRow([").append("new Date(").append(flow.get(Calendar.YEAR)).append(",").append(flow.get(Calendar.MONTH)).append(",")
            .append(flow.get(Calendar.DAY_OF_MONTH)).append(")");
            
            s.append(", ").append(Math.round(ideal[i]/(BudgetCustomizer.HOURS_IN_DAY * 3600d)));
            s.append(", ").append(Math.round(real[i]/(BudgetCustomizer.HOURS_IN_DAY * 3600d)));
            s.append("]);\n");    
         }
         
         
        s.append("var options = {\'title\':\'").append(securedTaskBean.getName()).append("\',\n");
 		s.append("\'backgroundColor\': \'transparent\'\n");
 		
 		s.append("};\n");
 		

 		s.append("		      var chart = new google.visualization.LineChart(document.getElementById(\'burndownchart_div").append(securedTaskBean.getId()).append("\'));\n");
 		s.append(" data.sort({column: 0});");
 		s.append(" var formatter = new google.visualization.DateFormat({pattern: \"").append(periods.getPattern()).append("\"});\n");
 		s.append(" formatter.format(data, 0);\n");
 		s.append("chart.draw(data, options);\n");
 		s.append("}\n");
 		s.append("</script>\n");
        return s.toString();
		} else return null;
		
	}
}
