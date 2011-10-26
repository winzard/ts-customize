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

public class AreaChart  implements TaskUDFValueScript {

    private int PERIODS = 40;

    public interface Periods{
        public int since(Calendar a, Calendar b);
        public String getTitle();
        public int getShift();
    }

    public class DayPeriods implements Periods {
    public String getTitle(){
        return "день";
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
        public String getTitle(){
        return "неделю";
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
        public String getTitle(){
        return "месяц";
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


                SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.SHORT, DateFormatter.getLocaleFromString(securedTaskBean.getSecure().getLocale()));

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


                ArrayList<SecuredTaskBean> children = securedTaskBean.getChildren();
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
                           /* if (since> lastPeriod){
                                for (int i = lastPeriod+1; i<since; i++){
                                       currentSpentTimeTable[i] = 0;
                                }
                            }*/

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

                StringBuffer buf = new StringBuffer();

                buf.append("<style class=\"text/css\">");
                buf.append("/*plugin styles*/\n" +
                        ".visualize { border: 1px solid #888; position: relative; background: #fafafa; }\n" +
                        ".visualize canvas { position: absolute; }\n" +
                        ".visualize ul,.visualize li { margin: 0; padding: 0;}\n" +
                        "\n" +
                        "/*pie labels*/\n" +
                        ".visualize-pie .visualize-labels { list-style: none; }\n" +
                        ".visualize-pie .visualize-label-pos, .visualize-pie .visualize-label { position: absolute;  margin: 0; padding:0; }\n" +
                        ".visualize-pie .visualize-label { display: block; color: #fff; font-weight: bold; font-size: 14px; }\n" +
                        ".visualize-pie-outside .visualize-label { color: #000; font-weight: normal; }"+
                        "/*table title, key elements*/\n" +
                        ".visualize .visualize-info { padding: 3px 5px; background: #fafafa; border: 1px solid #888; position: absolute; top: -20px; right: 10px; opacity: .8; }\n" +
                        ".visualize .visualize-title { display: block; color: #333; margin-bottom: 3px;  font-size: 1.1em; }\n" +
                        ".visualize ul.visualize-key { list-style: none;  }\n" +
                        ".visualize ul.visualize-key li { list-style: none; float: left; margin-right: 10px; padding-left: 10px; position: relative;}\n" +
                        ".visualize ul.visualize-key .visualize-key-color { width: 6px; height: 6px; left: 0; position: absolute; top: 50%; margin-top: -3px;  }\n" +
                        ".visualize ul.visualize-key .visualize-key-label { color: #000; }\n" +
                        "\n" +
                        "/*line,bar, area labels*/\n" +
                        ".visualize-labels-x,.visualize-labels-y { position: absolute; left: 0; top: 0; list-style: none; }\n" +
                        ".visualize-labels-x li, .visualize-labels-y li { position: absolute; bottom: 0; }\n" +
                        ".visualize-labels-x li span.label, .visualize-labels-y li span.label { position: absolute; color: #555;  }\n" +
                        ".visualize-labels-x li span.line, .visualize-labels-y li span.line {  position: absolute; border: 0 solid #ccc; }\n" +
                        ".visualize-labels-x li { height: 100%; }\n" +
                        ".visualize-labels-x li span.label { top: 100%; margin-top: 5px; }\n" +
                        ".visualize-labels-x li span.line { border-left-width: 1px; height: 100%; display: block; }\n" +
                        ".visualize-labels-x li span.line { border: 0;} /*hide vertical lines on area, line, bar*/\n" +
                        ".visualize-labels-y li { width: 100%;  }\n" +
                        ".visualize-labels-y li span.label { right: 100%; margin-right: 5px; display: block; width: 100px; text-align: right; }\n" +
                        ".visualize-labels-y li span.line { border-top-width: 1px; width: 100%; }\n" +
                        ".visualize-bar .visualize-labels-x li span.label { width: 100%; text-align: center; }\n"+

                        ".visualize { margin: 60px 0 0 30px; padding: 70px 40px 90px; background: #ccc url(../images/chartbg-vanilla.png) top repeat-x; border: 1px solid #ddd; -moz-border-radius: 12px; -webkit-border-radius: 12px; border-radius: 12px; }\n" +
                        ".visualize canvas { border: 1px solid #aaa; margin: -1px; background: #fff; }\n" +
                        ".visualize-labels-x, .visualize-labels-y { top: 70px; left: 40px; z-index: 100; }\n" +
                        ".visualize-pie .visualize-labels { position: absolute; top: 70px; left: 40px; }\n" +
                        ".visualize-labels-x li span.label, .visualize-labels-y li span.label { color: #444; font-size: 7px; padding-right: 5px; }\n" +
                        ".visualize-labels-y li span.line { border-style: solid;  opacity: .7; }\n" +
                        ".visualize .visualize-info { border: 0; position: static;  opacity: 1; background: none; }\n" +
                        ".visualize .visualize-title { position: absolute; top: 20px; color: #333; margin-bottom: 0; left: 20px; font-size: 2.1em; font-weight: bold; }\n" +
                        ".visualize ul.visualize-key { position: absolute; bottom: 15px; background: #eee; z-index: 10; padding: 10px 0; color: #aaa; width: 100%; left: 0;  }\n" +
                        ".visualize ul.visualize-key li { font-size: 1.2em;  margin-left: 20px; padding-left: 18px; }\n" +
                        ".visualize ul.visualize-key .visualize-key-color { width: 10px; height: 10px;  margin-top: -4px; }\n" +
                        ".visualize ul.visualize-key .visualize-key-label { color: #333; }"
                );
                buf.append("</style>");
                buf.append("<script type=\"text/javascript\" src=\"/TrackStudio/html/visualize/jquery.min.js\"></script>");
                buf.append("<script type=\"text/javascript\" src=\"/TrackStudio/html/visualize/visualize.jQuery.js\"></script>");
                buf.append("<table id=\"workprogress\" style=\"display: none\">");
                buf.append("<caption>Прогресс работы</caption>");
                    buf.append("<tr>");
                    buf.append("<td>");
                    buf.append("</td>");
                Calendar flow = (Calendar)startAt.clone();
                for (int i =0 ; i<= totalPeriods; i++){
      buf.append("<th scope=\"col\">");

                    flow.add(periods.getShift(), 1);
                    if (totalPeriods< 15 || i % 2 != 0)   {
                    String str = sdf.format(flow.getTime());
                    buf.append(str);
                    }
                    buf.append("</th>");

                }
                buf.append("</tr>");
                buf.append("<tr>");
                buf.append("<th scope=\"row\">");
                    buf.append("Всего часов");
                    buf.append("</th>");

                for (int i =0 ; i<= totalPeriods; i++){
                    buf.append("<td>");
                    buf.append(Math.round((float)summaryTable[i]/3600f));
                    buf.append("</td>");
                }
                buf.append("</tr>");
                buf.append("<tr>");
                buf.append("<th scope=\"row\">");
                    buf.append("Часов за "+periods.getTitle());
                    buf.append("</th>");                                                                                                   
                for (int i =0 ; i<= totalPeriods; i++){
                    buf.append("<td>");
                    buf.append(Math.round((float)spentTimeTable[i]/3600f));
                    buf.append("</td>");
                }
                buf.append("</tr>");
                buf.append("</table>");
                buf.append("<script>");
                buf.append("$('table#workprogress').visualize({type: 'area', width: '800px', height: '600px'});");
                buf.append("</script>");

                return buf.toString();


           } catch (Exception e) {
            e.printStackTrace();
            throw new GranException(e);
        }

     }
}
