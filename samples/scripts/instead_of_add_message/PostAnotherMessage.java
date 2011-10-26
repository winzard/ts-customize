package scripts.instead_of_add_message;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.*;
import com.trackstudio.tools.Null;


import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostAnotherMessage  implements OperationTrigger {

          
        public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException
        {
            String s = message.getDescription();

        String userStatusId = "d14883c11eef1ae9011eef360b6f0045"; //внешние пользователи
        //String userStatusId = task.getSubmitter().getPrstatusId();
        if (s != null && s.length() > 0) {
            String emailPattern = "From:\\s*\\\"?(\\S+\\s*\\S+[^\\\"])?\\\"?\\s+(<|&lt;)?(([-a-z0-9!#$%&'*+/=?^_`{|}~]+(\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*)@([a-z0-9.]+))(&gt;|>)?";
            // From: max.vasenkov@gmail.com
            // From: Maxim Vasenkov <max.vasenkov@gmail.com>
            // From: Maxim Vasenkov &lt;max.vasenkov@gmail.com&gt;
            // From: Winzard <i@winzard.ru>
            // From: Admin <admin@localhost>
            // From: "Максим Васенков" <vasenkov@any.place.com>
            // From: "Максим Васенков" &lt;vasenkov@any.place.com&gt;
            Pattern pat = Pattern.compile(emailPattern);
            Matcher mat = pat.matcher(s);
            SessionContext sc = message.getSecure();
            if (mat.find()) {
                String userName = mat.group(1);
                String userEmail = mat.group(3);
                if (userName == null) userName = mat.group(4);
                String fId = KernelManager.getUser().findUserIdByEmailNameProject(userEmail, userEmail, message.getTaskId());
                if (fId==null){
                String id = AdapterManager.getInstance().getSecuredUserAdapterManager().createUser(message.getSecure(),
                        Null.beNull(message.getSubmitterId()), userEmail, userName, Null.beNull(userStatusId));
                    AdapterManager.getInstance().getSecuredUserAdapterManager().updateUser(message.getSecure(), id, userEmail, userName, null, userEmail,
                            userStatusId, message.getSubmitterId(), message.getSecure().getUser().getTimezone(), message.getSecure().getUser().getLocale(), message.getSecure().getUser().getCompany(), null, null, null, null, true);
                String aclid = AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(message.getSecure(),
                        message.getTask().getParentId(), null, id, null);
                AdapterManager.getInstance().getSecuredAclAdapterManager().updateTaskAcl(message.getSecure(),
                        aclid, userStatusId, false);
                if (id != null) {
                    String sessionId = SessionManager.getInstance().create(UserRelatedManager.getInstance().find(id));
                    sc = SessionManager.getInstance().getSessionContext(sessionId);
                    String mstatusId = message.getMstatusId();
                    SecuredMessageTriggerBean createMessage = null;
            createMessage = new SecuredMessageTriggerBean(
                    null /* индентификатор */,
                    message.getDescription() /* текст комментария */,
                    Calendar.getInstance() /* время выполнения операции */,
                    null /* потраченное время */,
                    message.getDeadline() /* Сроки выполнения задачи (deadline) */,
                    message.getBudget() /* бюджет */,
                    message.getTaskId() /* задача */,
                    sc.getUserId() /* автор операции */,
                    null /* резолюция */,
                    message.getPriorityId() /* приоритет */,
                    message.getHandlerId() /* ответственные */,
                    message.getHandlerUserId() /* ответственный */,
                    message.getHandlerGroupId() /* ответственный, если нужно задать группу в качестве ответственного */,
                    mstatusId /* тип операции */,
                    null /* Map с дополнительными полями */,
                    sc /* SessionContext */,
                    null /* вложения */);
                    message = createMessage;
                }
            }

        }

        }
               return message.create(true);
        }
}
