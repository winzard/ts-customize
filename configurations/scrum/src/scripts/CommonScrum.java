package scripts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;
import java.io.FileReader;
import java.io.IOException;

/**
 * Общий класс, в котором определяются константы. Константы могут также читаться из файла scrum.properties. Его нужно разместить в папке с TrackStudio
 */
public class CommonScrum {
    public static Log log = LogFactory.getLog(CommonScrum.class);
    private static Properties properties=null;

    {
        try {
            properties = new Properties();
            properties.load(new FileReader("scrum.properties"));
        } catch (IOException e) {
            properties = null;

        }
    }

    protected String SCRUM_SPRINT_STATE_RUN = "297eef00298994cc01298eed0cdf00a7";
    protected String SCRUM_TEAM_MEMBER_GROUP = "297eef002988a01b012988a30e8600a2";
    protected String SCRUM_ITEM_OPERATION_ESTIMATE = "297eef002988a01b01298943bbf801a9";
    protected String SCRUM_ITEM_STATE_FINISH = "297eef002988a01b012988dea80b010c";
    protected String SCRUM_SRINT_CATEGORY = "297eef0029925d1f01299260b49b0002";
    protected String SCRUM_ITEM_OPERATION_RUN = "297eef00298994cc01298d9df1650004";
    protected String SCRUM_ITEM_OPERATION_STOP = "297eef0029927c5b012993cbbbf8018c";
    protected String SCRUM_ITEM_STATE_READY = "297eef002988a01b012988dea7ef010a";
    protected String SCRUM_ITEM_STATE_RUN = "297eef002988a01b012988dea809010b";
    protected String SCRUM_TEAM_WORKHOURS = "297eef0029b120360129b1e2f4e30076";
    protected String SCRUM_SPRINT_OPERATION_RUN="297eef0029927c5b0129928276860019";
    protected String SCRUM_TEAM_SEE_OTHERS="yes";

    public CommonScrum() {
        if (properties!=null){
        SCRUM_SPRINT_STATE_RUN = properties.getProperty("scrum.sprint.state.run");
        SCRUM_TEAM_MEMBER_GROUP = properties.getProperty("scrum.team.member.group");
        SCRUM_ITEM_OPERATION_ESTIMATE = properties.getProperty("scrum.item.operation.estimate");
        SCRUM_ITEM_STATE_FINISH = properties.getProperty("scrum.item.state.finish");
        SCRUM_SRINT_CATEGORY = properties.getProperty("scrum.sprint.category");
        SCRUM_ITEM_OPERATION_RUN =  properties.getProperty("scrum.item.operation.run");
        SCRUM_ITEM_OPERATION_STOP = properties.getProperty("scrum.item.operation.stop");
        SCRUM_ITEM_STATE_READY = properties.getProperty("scrum.item.state.ready");
        SCRUM_ITEM_STATE_RUN = properties.getProperty("scrum.item.state.run");
        SCRUM_TEAM_WORKHOURS = properties.getProperty("scrum.team.workhours");
        SCRUM_SPRINT_OPERATION_RUN = properties.getProperty("scrum.sprint.operation.run");
        SCRUM_TEAM_SEE_OTHERS = properties.getProperty("scrum.team.see.others");
        }
    }
}
