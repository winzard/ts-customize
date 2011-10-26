package scripts.task_custom_field_value;

import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;


import java.util.Calendar;

/**
 * Скрипт переводит задачи, находящиеся в состоянии "Новая" или "В процессе" или "Приостановлена"
 * в состояние "Просрочена" на следующий день после дедлайна.
 */
public class Escalate implements TaskUDFValueScript {

    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {
        Calendar deadline = securedTaskBean.getDeadline();
        String status = securedTaskBean.getStatus().getName();
        if (deadline != null && (status.equals("Новая") || status.equals("В процессе") || status.equals("Приостановлена"))) {
            Calendar now = Calendar.getInstance();

            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);


            deadline.set(Calendar.HOUR_OF_DAY, 0);
            deadline.set(Calendar.MINUTE, 0);
            deadline.set(Calendar.SECOND, 0);
            deadline.set(Calendar.MILLISECOND, 0);

            long l = now.getTimeInMillis() - deadline.getTimeInMillis();

            int days = (int) (l / (24 * 60 * 60 * 1000));
            if (days > 0) {
                // переводим
                String mstatusId = CSVImport.findMessageTypeIdByName("escalate", securedTaskBean.getCategory().getName());

                /**
                 * Создаем SecuredMessageTriggerBean
                 */
                SecuredMessageTriggerBean createMessage = new SecuredMessageTriggerBean(
                        null /* индентификатор */,
                        "задача была просрочена" /* текст комментария */,
                        Calendar.getInstance() /* время выполнения операции */,
                        null /* потраченное время */,
                        securedTaskBean.getDeadline() /* Сроки выполнения задачи (deadline) */,
                        securedTaskBean.getBudget() /* бюджет */,
                        securedTaskBean.getId() /* задача */,
                        securedTaskBean.getSecure().getUserId() /* автор операции */,
                        null /* резолюция */,
                        securedTaskBean.getPriorityId() /* приоритет */,
                        securedTaskBean.getHandlerId() /* ответственные */,
                        securedTaskBean.getHandlerUserId() /* ответственный */,
                        securedTaskBean.getHandlerGroupId() /* ответственный, если нужно задать группу в качестве ответственного */,
                        mstatusId /* тип операции */,
                        null /* Map с дополнительными полями */,
                        securedTaskBean.getSecure() /* SessionContext */,
                        null /* вложения */);
                /**
                 * выполняем
                 */
                createMessage.create(true);

                return "просрочена";
            }

        }

        return "в работе";
    }

}
