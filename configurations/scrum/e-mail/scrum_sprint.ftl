<#assign ContentType="text/plain;\n charset=\"${charset}\""/>
<#assign Subject="<${task.projectAlias} | Спринт стартовал: ${task.name}"/>
<#if fromUserEmail?exists>
    <#assign FromEmail="${fromUserEmail}"/>
</#if>
<#if fromUserName?exists>
    <#assign FromUser="${fromUserName}"/>
</#if>
<#assign Headers={"X-Meta":"data"}/>

Уважаемые спринтеры! Только что стартовал спринт ${task.name}.
Мы должны закончить его к ${DateFormatter.parse(task.deadline)}

Страница информации о спринте доступна по адресу:
${link}/task/${task.number}?thisframe=true


<@std.I18n key="CHANGE_NOTIFICATION" value=[source.getName(), source.getFilter(), source.getTask()]/>

<@std.I18n key="NOTIFICATION_UNSUBSCRIBE"/> ${link}/unsubscribe?notificationId=${source.id}
