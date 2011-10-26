package scripts.before_add_message;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.textfilter.AccentedFilter;

public class CreateSubtasks implements OperationTrigger {

    private static final String TODO = "ff80818131dbce630131dbe092910012";

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        String description = message.getDescription();
        StringBuffer newDescription = new StringBuffer();
        if (description.contains("*")) {
        String[] lines = description.split("<br />");
        for (String line: lines){
            if (line.trim().startsWith("*")){
                String taskName = line.trim().substring(1);
                if (taskName.length()>0){
                    StringBuffer b = new StringBuffer(taskName);
                    b = AccentedFilter.unescape(b);
                    String startState = "ff80818131d296560131d2974e4a0003";
                    if (message.getTask().getStatusId().equals("ff80818131cd4e930131cd506aa5000b")) startState="ff80818131e179ef0131e1e9306400e0";
                    String taskId = TriggerManager.getInstance().createTask(message.getSecure(), TODO, null, b.toString().trim(), "", null, null, null, message.getTaskId(), message.getHandlerUserId(), message.getHandlerGroupId(), true, null, startState, null);
                    if (taskId!=null) {
                        SecuredTaskBean t = new SecuredTaskBean(taskId, message.getSecure());
                        newDescription.append("##").append(t.getNumber()).append("<br/>");
                    }
                }

            } else newDescription.append(line).append("<br />");
        }
        message.setHandlerUserId(message.getTask().getHandlerUserId());
        message.setDescription(newDescription.toString());
        }
        return message;
    }
}
