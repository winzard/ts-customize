package scripts.before_add_message;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ChooseParticipants implements OperationTrigger {
    protected String WORKGROUP_UDF = "Исполнители";
    protected String WORKGROUP_ROLE = "ff80818133205e4c0133206404060002";

    protected static Properties properties = null;

    {
        try {
            properties = new Properties();
            properties.load(new FileReader("tander.properties"));
        } catch (IOException e) {
            properties = null;

        }
    }

    public ChooseParticipants() {
        if (properties != null) {
            WORKGROUP_UDF = properties.getProperty("workgrop.udf");

        }
    }

    public void subscribe(SecuredUserBean member, SecuredTaskBean task) throws GranException {
        SessionContext sc = member.getSecure();
        if (task != null) {
            List<SecuredTaskAclBean> targetAcl = AdapterManager.getInstance().getSecuredAclAdapterManager().getTaskAclList(sc,
                    task.getId());
            boolean exists = false;
            for (SecuredTaskAclBean tb : targetAcl) {
                if (tb.canManage() && tb.getTaskId().equals(task.getId())) {
                    SecuredUserBean user = tb.getUser();
                    SecuredPrstatusBean prstatus = tb.getPrstatus();
                    exists = (user.equals(member) && prstatus.getId().equals(WORKGROUP_ROLE));
                    if (exists) break;
                }
            }
            if (!exists) {
                // create
                String aclid =
                        AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, task.getId(), null,
                                member.getId(), null);
                AdapterManager.getInstance().getSecuredAclAdapterManager().updateTaskAcl(sc, aclid, WORKGROUP_ROLE, false);

            }
        }
    }

    public void unsubscribe(SecuredUserBean member, SecuredTaskBean task) throws GranException {
        SessionContext sc = member.getSecure();
        if (task != null) {
            List<SecuredTaskAclBean> targetAcl = AdapterManager.getInstance().getSecuredAclAdapterManager().getTaskAclList(sc,
                    task.getId());
            boolean exists = false;
            for (SecuredTaskAclBean tb : targetAcl) {
                if (tb.canManage() && tb.getTaskId().equals(task.getId())) {
                    SecuredUserBean user = tb.getUser();
                    SecuredPrstatusBean prstatus = tb.getPrstatus();
                    exists = (user.equals(member) && prstatus.getId().equals(WORKGROUP_ROLE));
                    if (exists) {
                        AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, tb.getId());
                        break;
                    }
                }
            }
        }
    }



    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        ArrayList<SecuredUserBean> users = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(message.getSecure(), message.getTaskId());
        SecuredTaskBean task = message.getTask();
        String fellowsUDF = message.getUdfValue(WORKGROUP_UDF);
        if (fellowsUDF != null) {
            String[] value = fellowsUDF.split(";");
            if (value != null && value.length > 0)
                for (String login : value) {
                    if (login.length() > 0) {
                        SecuredUserBean f = AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(message.getSecure(), login.substring(1)); //skip @
                        if (f != null) {
                            subscribe(f, task);
                            users.remove(f);
                        }
                    }
                }
            for (SecuredUserBean u: users)
            unsubscribe(u, task);
        }

        return message;
    }


}
