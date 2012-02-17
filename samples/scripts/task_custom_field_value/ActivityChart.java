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

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTransitionBean;
import com.trackstudio.tools.tree.OrderedTree;
/**
 * График активности по задачам. Показывает изменение количества задач и их состояния с течением времени.
 *  В качестве начальной точки берется дата создания самой старой задачи
 *  @author winzard
 */
public class ActivityChart extends AreaChart {
	
	
	@Override
	
	public Object calculate(SecuredTaskBean securedTaskBean)
			throws GranException {
		
        
        
        
        
        
        HashMap<SecuredStatusBean, Integer[]> chartTable = new HashMap<SecuredStatusBean, Integer[]>();

		ArrayList<SecuredTaskBean> children = securedTaskBean.getChildren();
		ArrayList<SecuredStatusBean> states = new ArrayList<SecuredStatusBean>();
		if (children!=null && !children.isEmpty()){
		// period, state, quantity
		
		int lastPeriod = 0;
		Collections.sort(children, new Comparator<SecuredTaskBean>() {

			@Override
			public int compare(SecuredTaskBean o1, SecuredTaskBean o2) {
				try{
				int j = o1.getSubmitdate().compareTo(o2.getSubmitdate());
				if (j!=0) return j;
				else return o1.compareTo(o2);
				} catch (GranException ge) {
					return -1;
				}
			}
			
		});
		
		Calendar startAt = children.get(0).getSubmitdate();

        Calendar endAt = securedTaskBean.getUpdatedate();
        Periods periods = new DayPeriods();
        int totalPeriods = periods.since(endAt, startAt);
        if (totalPeriods> PERIODS){
            periods = new WeekPeriods();
            totalPeriods = periods.since(endAt, startAt);
            if (totalPeriods>PERIODS){
                periods = new MonthPeriods();
                totalPeriods = periods.since(endAt, startAt);
            }
        }
        for (SecuredTaskBean task : children) {
            SecuredStatusBean[] currentStatesTable = new SecuredStatusBean[totalPeriods+1];
            //Arrays.fill(currentStatesTable, "");
            
            SecuredStatusBean prev = null, now = null;
            lastPeriod = 0;
            
            ArrayList<SecuredMessageBean> messages = task.getMessages();
            if (messages==null || messages.isEmpty()){
            	int start = periods.since( task.getSubmitdate(), startAt);
            	for( int i=start; i<= totalPeriods; i++){
                    currentStatesTable[i] = task.getStatus();
                }
            	lastPeriod = totalPeriods;
            } else
			for (SecuredMessageBean msg : messages) {
            	if (prev==null){
            		SecuredTransitionBean t = nextState(msg.getMstatus(), prev);
            		if (t!=null){
            		prev = t.getStart();
            		now = t.getFinish();
            	int since = periods.since( msg.getTime(), startAt);
            	int start = periods.since( task.getSubmitdate(), startAt);
            	for (int i = start; i<since; i++){
         			currentStatesTable[i] = prev;
                 }
            	currentStatesTable[since] = now;
            	lastPeriod = since;
            	prev = now;
            		}
            	} else{
            		SecuredTransitionBean t = nextState(msg.getMstatus(), prev);
            		if (t!=null){
            		now = t.getFinish();
            		prev = t.getStart();
            		} else now = prev;
            		int since = periods.since( msg.getTime(), startAt);
                    if (since==lastPeriod){
                    currentStatesTable[since] = now;
                 } else {
                     	currentStatesTable[since]=now;
                     	if (since> lastPeriod){
                     		for (int i = lastPeriod; i<since; i++){
                     			currentStatesTable[i] = prev;
                             }
                     	}
                         lastPeriod = since;
                         prev = now;
                 }
            	}
            }
            
            if (lastPeriod< totalPeriods+1) for( int i=lastPeriod+1; i<= totalPeriods; i++){
                currentStatesTable[i] = currentStatesTable[lastPeriod];
            }
            for (int i = 0; i<=totalPeriods; i++){
            	if (currentStatesTable[i]!=null){
            	if (chartTable.get(currentStatesTable[i])==null){
            		if (!states.contains(currentStatesTable[i])){
            			states.add(currentStatesTable[i]);
            		}
            		Integer[] array = new Integer[totalPeriods+1];
            		Arrays.fill(array, 0);
            		chartTable.put(currentStatesTable[i], array);
            	}
                chartTable.get(currentStatesTable[i])[i]++;
            	}
            }
        }
        StringBuffer s = new StringBuffer();
        s.append("<div id=\"stackedchart_div").append(securedTaskBean.getId()).append("\"></div>\n");
        s.append("<script type=\"text/javascript\">\n");
        s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
        s.append("google.setOnLoadCallback(drawStackedChart").append(securedTaskBean.getId()).append(");\n");
        s.append("function drawStackedChart").append(securedTaskBean.getId()).append("() {\n");
        s.append("var data = new google.visualization.DataTable();\n");
        s.append("data.addColumn(\'date\', \'").append("Период").append("\');\n");
        for (SecuredStatusBean state: states)
        s.append("data.addColumn(\'number\', \'").append(state.getName()).append("\');\n");
         Calendar flow = (Calendar)startAt.clone();
         	 
         for (int i =0 ; i<= totalPeriods; i++){
         	flow.add(periods.getShift(), 1);      
            s.append("data.addRow([").append("new Date(").append(flow.get(Calendar.YEAR)).append(",").append(flow.get(Calendar.MONTH)).append(",")
            .append(flow.get(Calendar.DAY_OF_MONTH)).append(")");
            for (SecuredStatusBean b : states){
                Integer[] calculations = chartTable.get(b);   
            s.append(", ").append(calculations[i]);
            }
            s.append("]);\n");    
         }
         
         
        s.append("var options = {\'title\':\'").append(securedTaskBean.getName()).append("\',\n");
 		s.append("\'backgroundColor\': \'transparent\',\n");
 		s.append("\'isStacked\': true,\n");
 		s.append("\'colors\': [");
 		for (SecuredStatusBean state: states)
 	        s.append("\'").append(state.getColor()).append("\', ");
 		s.append("\'black\']\n");
 		
 		s.append("};\n");
 		

 		s.append("		      var chart = new google.visualization.ColumnChart(document.getElementById(\'stackedchart_div").append(securedTaskBean.getId()).append("\'));\n");
 		s.append(" data.sort({column: 0});");
 		s.append(" var formatter = new google.visualization.DateFormat({pattern: \"").append(periods.getPattern()).append("\"});\n");
 		s.append(" formatter.format(data, 0);\n");
 		s.append("chart.draw(data, options);\n");
 		s.append("}\n");
 		s.append("</script>\n");
        return s.toString();
		} else return null;
		
	}

	private SecuredTransitionBean nextState(SecuredMstatusBean msg, SecuredStatusBean prev) throws GranException {
		ArrayList<SecuredTransitionBean> tr = msg.getTransitions();
		
		for (SecuredTransitionBean t: tr){
			if (prev==null)
				if (t.getStart().isStart()) {
					return t;
				}
				else if (t.getStart().equals(prev)) return t;
			}
		if (tr.size()>0){
			return tr.get(0);
		}
		return null;
	}
	 
}
