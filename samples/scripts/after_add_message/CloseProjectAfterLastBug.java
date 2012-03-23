package scripts.after_add_message;

import java.util.HashMap;
import java.util.List;


import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
/*
 * Триггер закрывает проект автоматически после того, как в нем закрыта последняя задача
 */
public class CloseProjectAfterLastBug implements OperationTrigger {
	/**
	 * Тип операции, которая будет выполняться для закрытия проекта. Введите здесь свой ID перед компиляцией.<br>
	 * У пользователя, который будет выполнять операцию закрытия задачи, должны также быть права и на закрытие проекта.
	 * Триггер следует назначить на операцию закрытия задачи (ошибки)
	 * 
	 */
	private static final String CLOSE_PROJECT_OPERATION = "4028808a193230e301193279870e0062";
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
    	// описание для операции закрытия проекта берется из текущей операции, но это можно поменять
    	String text = message.getDescription();
        SecuredTaskBean task = message.getTask();
        if (task.getParent()!=null){
        SecuredTaskBean parent = task.getParent();
        List<SecuredTaskBean> tasks = parent.getChildren();
        boolean allDone = true;
        for (SecuredTaskBean p : tasks) {
        	// Сама закрываемая задача еще не изменила состояние
        	if (!p.getId().equals(task.getId()) && !p.getStatus().isFinish())
        	{
        		allDone = false;
        		break;
        	}
        }
        if (allDone) {
        if (AdapterManager.getInstance().getSecuredStepAdapterManager().getNextStatus(task.getSecure(), task.getId(), CLOSE_PROJECT_OPERATION)!=null)
            	        TriggerManager.getInstance().createMessage(task.getSecure(), parent.getId(), CLOSE_PROJECT_OPERATION, text, null, parent.getHandlerUserId(), parent.getHandlerGroupId(), null, parent.getPriorityId(), parent.getDeadline(), parent.getBudget(), null, true, null );
            	        
            }
        }

        return message;
    }


}

