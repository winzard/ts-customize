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
                buf.append("<table id=\"workhours\" style=\"display: none\">");
                buf.append("<caption>Рабочие часы</caption>");
        buf.append("<tr>");
                buf.append("<th scope=\"row\">");
                    buf.append("Рабочие часы");
                    buf.append("</th>");
                    buf.append("<td>");
                    buf.append("</td>");
                 buf.append("</tr>");
                for (SecuredUserBean u :participants.keySet()){
                buf.append("<tr>");
                buf.append("<th scope=\"row\">");
                    buf.append(u.getName());
                    buf.append("</th>");
                    buf.append("<td>");
                    buf.append((float)participants.get(u)/3600f);
                    buf.append("</td>");
                 buf.append("</tr>");
                }

                buf.append("</table>");
                buf.append("<script>");
                buf.append("$('table#workhours').visualize({type: 'bar', width: '800px', height: '600px'});");
                buf.append("</script>");

                return buf.toString();
    }
}
