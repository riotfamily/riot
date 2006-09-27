<#import "/spring.ftl" as spring />

<div id="notifications" class="box">
	<div class="title">
		<div class="icon"></div>
		<span><@spring.messageText "label.notifications.title", "Messages" /></span>
	</div>
	<div id="notificationList">
		<div class="notification">
			<@spring.messageText "label.notifications.emptyList", "No new messages" />					
		</div>
	</div>
	<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/notifications.js"></script>
	<script type="text/javascript" language="JavaScript">
		new NotificationList('notificationList', '${url(servletPrefix + '/notifications')}');			
	</script>				
</div>	