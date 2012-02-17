package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.formatter.DateFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
/**
 * Количество потраченных часов в за период (дни, недели, месяцы - в зависимости от длительности проекта) и сумма этих часов
 * @author winzard
 *
 */
public class AreaChart  implements TaskUDFValueScript {

    protected int PERIODS = 40;

    public interface Periods{
        public int since(Calendar a, Calendar b);
        public String getPattern();
        public int getShift();
    }

    public class DayPeriods implements Periods {
    public String getPattern(){
        return "dd.MM.yy";
    }

    public int getShift(){
        return Calendar.DAY_OF_YEAR;
    }

    public int since(Calendar a, Calendar b){
        Calendar one = (Calendar)a.clone();
        Calendar two = (Calendar)b.clone();


            one.set(Calendar.HOUR_OF_DAY, 0);
            one.set(Calendar.MINUTE, 0);
            one.set(Calendar.SECOND, 1);
            one.set(Calendar.MILLISECOND, 0);
            two.set(Calendar.HOUR_OF_DAY, 0);
            two.set(Calendar.MINUTE, 0);
            two.set(Calendar.SECOND, 1);
            two.set(Calendar.MILLISECOND, 0);

            long l = one.getTimeInMillis() - two.getTimeInMillis();

            return  (int) (l / (24L * 3600000L));
    }
    }
    public class WeekPeriods implements Periods {
        public String getPattern(){
        return "dd.MM.yy";
    }
        public int getShift(){
        return Calendar.WEEK_OF_YEAR;
    }

    public int since(Calendar a, Calendar b){
        Calendar one = (Calendar)a.clone();
        Calendar two = (Calendar)b.clone();

            one.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            one.set(Calendar.HOUR_OF_DAY, 0);
            one.set(Calendar.MINUTE, 0);
            one.set(Calendar.SECOND, 1);
            one.set(Calendar.MILLISECOND, 0);

            two.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            two.set(Calendar.HOUR_OF_DAY, 0);
            two.set(Calendar.MINUTE, 0);
            two.set(Calendar.SECOND, 1);
            two.set(Calendar.MILLISECOND, 0);

            long l = one.getTimeInMillis() - two.getTimeInMillis();

            return  (int) (l / (24L * 3600000L* 7L));
    }
    }

    public class MonthPeriods implements Periods {
        public String getPattern(){
        return "MMM yyyy";
    }

        public int getShift(){
        return Calendar.MONTH;
    }
    public int since(Calendar a, Calendar b){
        Calendar one = (Calendar)a.clone();
        Calendar two = (Calendar)b.clone();

            one.set(Calendar.DAY_OF_MONTH, 1);
            one.set(Calendar.HOUR_OF_DAY, 0);
            one.set(Calendar.MINUTE, 0);
            one.set(Calendar.SECOND, 1);
            one.set(Calendar.MILLISECOND, 0);

            two.set(Calendar.DAY_OF_MONTH, 1);
            two.set(Calendar.HOUR_OF_DAY, 0);
            two.set(Calendar.MINUTE, 0);
            two.set(Calendar.SECOND, 1);
            two.set(Calendar.MILLISECOND, 0);

            long l = one.getTimeInMillis() - two.getTimeInMillis();

            return  (int) (l / (24L * 3600000L* one.getActualMaximum(Calendar.DAY_OF_MONTH)));
    }
    }

     public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {
    	 
          try {
        	  ArrayList<SecuredTaskBean> children = securedTaskBean.getChildren();
        	  if (children==null || children.isEmpty()) return null;
                Calendar startAt = securedTaskBean.getSubmitdate();

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

                long[] spentTimeTable = new long[totalPeriods+1];


                
                
                for (SecuredTaskBean task : children) {
                    long[] currentSpentTimeTable = new long[totalPeriods+1];
                    Arrays.fill(currentSpentTimeTable, 0L);
                    long spentTime = 0L;
                    int lastPeriod = 0;
                    for (SecuredMessageBean msg : task.getMessages()) {
                        if (msg.getHrs()!=null) {
                            spentTime=msg.getHrs();
                        }  else spentTime=0;

                        if (msg.getTime().before(startAt) || (periods.since(msg.getTime(), startAt)==0)){
                           currentSpentTimeTable[0] = currentSpentTimeTable[0]+spentTime;
                            lastPeriod = 0;
                        } else {
                            int since = periods.since( msg.getTime(), startAt);
                            	currentSpentTimeTable[since]=currentSpentTimeTable[since]+ spentTime;
                                lastPeriod = since;
                        }

                    }

                    for (int i = 0; i<=totalPeriods; i++){
                        spentTimeTable[i]+=currentSpentTimeTable[i];
                    }
                }
                long[] summaryTable = new long[totalPeriods+1];
                summaryTable[0]+=spentTimeTable[0];
                for (int i = 1; i<=totalPeriods; i++){
                        summaryTable[i]+=summaryTable[i-1]+spentTimeTable[i];
                    }
 
               StringBuffer s = new StringBuffer();
               s.append("<div id=\"areachart_div").append(securedTaskBean.getId()).append("\"></div>\n");
               s.append("<script type=\"text/javascript\">\n");
               s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
               s.append("google.setOnLoadCallback(drawArea").append(securedTaskBean.getId()).append(");\n");
               s.append("function drawArea").append(securedTaskBean.getId()).append("() {\n");
               s.append("var data = new google.visualization.DataTable();\n");
               s.append("data.addColumn(\'date\', \'").append("Период").append("\');\n");
               s.append("data.addColumn(\'number\', \'").append("Часов за период").append("\');\n");
               s.append("data.addColumn(\'number\', \'").append("Всего").append("\');\n");
                Calendar flow = (Calendar)startAt.clone();
                s.append("data.addRow([").append("new Date(").append(flow.get(Calendar.YEAR)).append(",").append(flow.get(Calendar.MONTH)).append(",").append(flow.get(Calendar.DAY_OF_MONTH)).append(")").append(", ").append(0).append(", ").append(0).append("]);\n");
                for (int i =0 ; i<= totalPeriods; i++){
                	flow.add(periods.getShift(), 1);      
                          //if (spentTimeTable[i]>0)
                    s.append("data.addRow([").append("new Date(").append(flow.get(Calendar.YEAR)).append(",").append(flow.get(Calendar.MONTH)).append(",").append(flow.get(Calendar.DAY_OF_MONTH)).append(")").append(", ").append(Math.round((float)spentTimeTable[i]/3600f)).append(", ").append(Math.round((float)summaryTable[i]/3600f)).append("]);\n");
                    
                }
                s.append("var options = {\'title\':\'").append(securedTaskBean.getName()).append("\',\n");
        		s.append("\'backgroundColor\': \'transparent\'\n");
        		s.append("};\n");
        		

        		s.append("		      var chart = new google.visualization.AreaChart(document.getElementById(\'areachart_div").append(securedTaskBean.getId()).append("\'));\n");
        		s.append(" data.sort({column: 0});");
        		s.append(" var formatter = new google.visualization.DateFormat({pattern: \"").append(periods.getPattern()).append("\"});\n");
        		s.append(" formatter.format(data, 0);\n");
        		s.append("chart.draw(data, options);\n");
        		s.append("}\n");
        		s.append("</script>\n");
                return s.toString();


           } catch (Exception e) {
            e.printStackTrace();
            throw new GranException(e);
        }

     }
}
