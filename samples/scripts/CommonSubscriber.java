package scripts;


import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.model.Notification;
import com.trackstudio.secured.SecuredUserBean;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CommonSubscriber {
    protected String FELLOWS_UDF = "";
    protected String SUBSCRIBE_FILTER_ID = "1";
    protected static Properties properties = null;

    {
        try {
            properties = new Properties();
            properties.load(new FileReader("subscribe.properties"));
        } catch (IOException e) {
            properties = null;

        }
    }

    public CommonSubscriber() {
        if (properties != null) {
            FELLOWS_UDF = properties.getProperty("subscribe.udf");
            SUBSCRIBE_FILTER_ID = properties.getProperty("subscribe.filter.id");
        }
    }

    protected void subscribe(SecuredUserBean user, String taskId) throws GranException {

        KernelManager.getFilter().setNotification(SafeString.createSafeString("auto"), SUBSCRIBE_FILTER_ID, user.getId(), null, taskId, user.getTemplate());
    }

    protected void unsubscribe(String taskId, List<String> toRemoveUsers) throws GranException {
        List<Notification> list = KernelManager.getFilter().getNotificationList(SUBSCRIBE_FILTER_ID, taskId);
        List<String> toRemove = new ArrayList<String>();
        for (Notification n : list) {
            if (n.getUser().getUser() != null && toRemoveUsers.contains(n.getUser().getUser().getId()))
                toRemove.add(n.getId());
        }
        for (String id : toRemove) KernelManager.getFilter().deleteNotification(id);
    }
}
