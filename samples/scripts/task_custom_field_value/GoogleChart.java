package scripts.task_custom_field_value;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;

public class GoogleChart implements TaskUDFValueScript {

	@Override
	public Object calculate(SecuredTaskBean task) throws GranException {

		StringBuffer s = new StringBuffer();
		s.append("   <div id=\"chart_div\" style=\"width:400; height:300\"></div>\n");
		s.append("<script type=\"text/javascript\">\n");
		s.append("function drawChart() {\n");

		s.append("var data = new google.visualization.DataTable();\n");
		s.append("data.addColumn(\'string\', \'Topping\');\n");
		s.append("data.addColumn(\'number\', \'Slices\');\n");
		s.append("data.addRows([\n");
		s.append("[\'Mushrooms\', 3],\n");
		s.append("[\'Onions\', 1],\n");
		s.append("[\'Olives\', 1],\n");
		s.append("[\'Zucchini\', 1],\n");
		s.append("[\'Pepperoni\', 2]\n");
		s.append("]);\n");

		s.append("var options = {\'title\':\'How Much Pizza I Ate Last Night\',\n");
		s.append("\'width\':400,\n");
		s.append("\'height\':300};\n");

		s.append("		      var chart = new google.visualization.PieChart(document.getElementById(\'chart_div\'));\n");
		s.append("chart.draw(data, options);\n");
		s.append("}\n");
		s.append("drawChart();\n");
		s.append("</script>\n");

		
		return s.toString();
	}

}
