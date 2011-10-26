package scripts.task_custom_field_lookup;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFLookupScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

import java.util.ArrayList;
import java.util.List;

public class WorkersList implements TaskUDFLookupScript {


    public Object calculate(SecuredTaskBean task) throws GranException {
        List<String> list = new ArrayList<String>();
        list.add("");
        ArrayList<SecuredUserBean> lis = task.getSecure().getUser().getChildren();
        for (SecuredUserBean t: lis){
                list.add(t.getName());
        }                                             
        return list;
    }
}