package scripts.before_add_message;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class changes a handler in task by email submission
 */
public class ChangeHandlerByEmail implements OperationTrigger {

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        String text  = message.getDescription();
        SecuredTaskBean task = message.getTask();
if (text != null && text.length() > 0) {
            String emailPattern = "To:\\s*\\\"?(\\S+\\s*\\S+[^\\\"])?\\\"?\\s+(<|&lt;)?(([-a-z0-9!#$%&'*+/=?^_`{|}~]+(\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*)@([a-z0-9.]+))(&gt;|>)?";
            // From: max.vasenkov@gmail.com
            // From: Maxim Vasenkov <max.vasenkov@gmail.com>
            // From: Maxim Vasenkov &lt;max.vasenkov@gmail.com&gt;
            // From: Winzard <i@winzard.ru>
            // From: Admin <admin@localhost>
            // From: "Максим Васенков" <vasenkov@any.place.com>
            // From: "Максим Васенков" &lt;vasenkov@any.place.com&gt;
            String usernamePattern = "To:\\s*(.+)";
            Pattern emailPat = Pattern.compile(emailPattern);
            Matcher emailMat = emailPat.matcher(text);
            Pattern usernamePat = Pattern.compile(usernamePattern);
            Matcher usernameMat = usernamePat.matcher(text);
            String foundUserId = null;
            if (emailMat.find()) {
                // found someone by email
                String userName = emailMat.group(1);
                String userEmail = emailMat.group(3);
                if (userName == null) userName = emailMat.group(4);
                foundUserId = KernelManager.getUser().findUserIdByEmailNameProject(userEmail, userName, task.getParentId());
            }
            if (foundUserId==null && usernameMat.find()){
                String userName = usernameMat.group(1);
                foundUserId = KernelManager.getUser().findUserIdByQuickGo(userName);
            }
        if (foundUserId!=null) message.setHandlerUserId(foundUserId);
        }

            return message;
        }
}
