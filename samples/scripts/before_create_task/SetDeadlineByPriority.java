package scripts.before_create_task;

import java.util.Calendar;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;

/**
 * Скрипт устанавливает deadline (Сделать до) задачи в зависимости от выбранного приоритета
 */
public class SetDeadlineByPriority implements TaskTrigger {

    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        if (task.getPriority().getName().equals("Обычный")) {
            Calendar now = Calendar.getInstance();
            // прибавляем 8 дней
            now.add(Calendar.DAY_OF_YEAR, 8);
            task.setDeadline(now);
        } else if (task.getPriority().getName().equals("Важный")) {
            Calendar now = Calendar.getInstance();
            // прибавляем 3 дня
            now.add(Calendar.DAY_OF_YEAR, 3);
            task.setDeadline(now);
        } else if (task.getPriority().getName().equals("Низкий")) {
            Calendar now = Calendar.getInstance();
            // прибавляем 14 дней
            now.add(Calendar.DAY_OF_YEAR, 14);
            task.setDeadline(now);
        }
        return task;
    }
}
