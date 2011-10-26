package scripts.before_add_message;


import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredUDFBean;

import java.util.HashMap;


public class BlockNoReason  implements OperationTrigger {
    private static String UNSATISFACTORY ="ff80818131cd4e930131cd62d3b80047";
    private static String SATISFACTORY ="ff80818131cd4e930131cd62d3b70046";
    private static String UDFID = "ff80818131cd4e930131cd62d39a0042";
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(message.getSecure(), UDFID);
        HashMap<String, String> ul= udf.getUL();
        String sat = ul.get(SATISFACTORY);
        String unsat = ul.get(UNSATISFACTORY);
        String value = message.getUdfValue(udf.getCaption());
        if ((value.equals(unsat) || value.equals(sat)) && message.getDescription().length()<10) throw new UserException("Вы должны указать причину выставления оценки исполнения", false);
        return message;
    }
}
