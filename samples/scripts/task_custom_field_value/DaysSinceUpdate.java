package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;

import java.util.Calendar;

/**
 * Возвращает число дней, прошедших с момента последнего обновления незакрытой задачи. Закрытой считается задача, находящаяся в финальном состоянии.
 * Для нее возвращается 0
 */
public class DaysSinceUpdate implements TaskUDFValueScript {
    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {
        if (!securedTaskBean.getStatus().isFinish()) {
            Calendar now = Calendar.getInstance();

            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            Calendar update = securedTaskBean.getUpdatedate();
            update.set(Calendar.HOUR_OF_DAY, 0);
            update.set(Calendar.MINUTE, 0);
            update.set(Calendar.SECOND, 0);
            update.set(Calendar.MILLISECOND, 0);

            long l = now.getTimeInMillis() - update.getTimeInMillis();

            return (int) (l / (24 * 60 * 60 * 1000));
        } else return 0;
    }
}
