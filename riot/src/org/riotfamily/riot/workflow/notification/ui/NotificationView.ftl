<#import "/spring.ftl" as spring />

<div id="notifications" class="box">
	<div class="box-title">
		<span class="label"><@spring.messageText "label.notifications.title", "Messages" /></span>
	</div>
	<div id="notificationList">
		<div class="notification">
			<@spring.messageText "label.notifications.emptyList", "No new messages" />
		</div>
	</div>
	<@riot.script src="notifications.js" />
	<script type="text/javascript" language="JavaScript">
		new NotificationList('notificationList', '${riot.href("/notifications")}');
	</script>
</div>