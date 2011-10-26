package scripts.bulk;

import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;

import java.util.Calendar;

/**
 * Выполняет операцию Note над указанной задачей
 */
public class ProcessNoteOperation implements TaskBulkProcessor {


    public SecuredTaskBean execute(SecuredTaskBean securedTaskBean) throws GranException {
        /**
         * Ищем идентификатор операции
         */
        String mstatusId = CSVImport.findMessageTypeIdByName("Комментарий", securedTaskBean.getCategory().getName());
        /**
         * Создаем SecuredMessageTriggerBean 
         */
        SecuredMessageTriggerBean createMessage = new SecuredMessageTriggerBean(
                null /* индентификатор */,
                "" /* текст комментария */,
                Calendar.getInstance() /* время выполнения операции */,
                null /* потраченное время */,
                null /* Сроки выполнения задачи (deadline) */,
                null /* бюджет */,
                securedTaskBean.getId() /* задача */,
                securedTaskBean.getSecure().getUserId() /* автор операции */,
                null /* резолюция */,
                null /* приоритет */,
                null /* ответственные */,
                null /* ответственный */,
                null /* ответственный, если нужно задать группу в качестве ответственного */,
                mstatusId /* тип операции */,
                null /* Map с дополнительными полями */,
                securedTaskBean.getSecure() /* SessionContext */,
                null /* вложения */);
        /**
         * выполняем
         */
        createMessage.create(true);
        /**
         * нужно вернуть SecuredTaskBean
         */
        return securedTaskBean;
    }
}
