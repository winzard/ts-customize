package scripts.before_add_message;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class changes a workflow custom field via email submission
 */
public class ChangeUDFByEmail implements OperationTrigger {

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        String fieldName = "Somefield";
        String text = message.getDescription();
        SecuredTaskBean task = message.getTask();
        if (text != null && text.length() > 0) {
            String udfPattern = fieldName + ":\\s*(.+)";
            Pattern udfPat = Pattern.compile(udfPattern);
            Matcher udfMat = udfPat.matcher(text);
            if (udfMat.find()) {
                String value = udfMat.group(1);
                message.getUdfValues().put(fieldName, value);
            }
        }
        return message;

    }
}
