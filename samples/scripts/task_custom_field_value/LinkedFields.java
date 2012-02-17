package scripts.task_custom_field_value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;

public class LinkedFields implements TaskUDFValueScript {

	@Override
	public Object calculate(SecuredTaskBean task) throws GranException {

		StringBuffer b = new StringBuffer();
		b.append("<select name=\"selectme\" id=\"selectme\">\r\n");
		b.append("<option value=\"\">-- select --</option>\r\n");
		b.append("<option value=\"1\" title=\"flowers\">Flowers</option>\r\n");
		b.append("<option value=\"2\" title=\"animals\">Animals</option>\r\n");
		b.append("</select>\r\n");

		b.append("<select name=\"selectme2\" id=\"selectme2\">\r\n");
		b.append("<option value=\"\">-- select --</option>\r\n");
		b.append("<option value=\"1\" class=\"flowers\">Sunflower</option>\r\n");
		b.append("<option value=\"2\" class=\"flowers\">Rose</option>\r\n");
		b.append("<option value=\"3\" class=\"animals\">Dog</option>\r\n");
		b.append("<option value=\"4\" class=\"animals\">Cat</option>\r\n");
		b.append("</select>\r\n");

		b.append("<script>\r\n");
		b.append("new Dependent($(\'selectme2\'),$(\'selectme\'));\r\n");
		b.append("</script>\r\n");

		return b.toString();
	}

}
