<div class="box loginStatus">
	<@c.message "label.status.hello">Hello</@c.message> <span class="userName">${sessionData.userName!}</span>.
	<@c.message "label.status.lastLogin">Last login</@c.message>: ${(sessionData.lastLoginDate?datetime)!} [${sessionData.lastLoginIP!}]
</div>