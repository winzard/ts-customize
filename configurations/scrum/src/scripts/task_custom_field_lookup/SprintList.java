package scripts.task_custom_field_lookup;

import com.trackstudio.external.TaskUDFLookupScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.exception.GranException;
import com.trackstudio.app.adapter.AdapterManager;

import java.util.List;

import java.util.ArrayList;

import scripts.CommonScrum;

/**
 * Выводит список спринтов для выбора
 */
public class SprintList extends CommonScrum implements TaskUDFLookupScript{


    public Object calculate(SecuredTaskBean task) throws GranException {
        List<String> list = new ArrayList<String>();
        list.add("");
        String category  = SCRUM_SRINT_CATEGORY;
        List<SecuredTaskBean> sprints = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskListByQuery(task.getSecure(),
        "SELECT t.id FROM com.trackstudio.model.Task as t WHERE t.category.id = \'"+category+"\'");
        for (SecuredTaskBean t: sprints){
            if (t.canView() && !t.getStatus().isFinish()){
                list.add(t.getName()+" [#"+t.getNumber()+"]");
            }
        }
        

        return list;
    }
}
