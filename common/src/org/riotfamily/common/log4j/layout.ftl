<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Riot Error Log</title>
<style type="text/css">
body, tt, td, th {
	font-family: "Bitstream Vera Sans Mono";
	font-size: 11px;
	color: #000;
}
a {
	color: #3465a4;
}
tt {
	display: block;
	margin: 5px 0;
}
table {
	display: block;
	margin-top: 10px;
}
	table, td, th {
		border: none;
	}
	th {
		padding: 0 5px 0 0;
		text-align: left;
		font-weight: bold;
	}

.entry {
	font-family: Bitstream Vera Sans;
	background: #ececec;
	padding: 5px 5px 5px 90px;
	margin: 5px;
	border-left: 5px solid #ccc;
}
	.entry:hover {
		background: #f4f4f4;
	}
	
	.debug {
		border-color: #5c3566;
	}
	.info {
		border-color: #4e9a06;
	}
	.warn {
		border-color: #edd400;
	}
	.error {
		border-color: #f57900;
	}
	.fatal {
		border-color: #cc0000;
	}


.time {
	margin: 0 4px 0 -84px;
}
.category {
	font-weight: bold;
	margin-right: 5px;
}

.toggle {
	text-decoration: underline;
	cursor: pointer;
	color: #aaa;
}
	.toggle:hover,
	.toggle:active {
		color: #3465a4;
	}

.stack,
.details {
	display: none;
}
<#list events as event>#d${event_index}:target, #s${event_index}:target<#if event_has_next>,<#else>{</#if> </#list>
	display: block
}

</style>
</head>
<body>

<#list events as event>
	<div class="entry ${event.level?lower_case}">
		<span class="time">${helper.toDate(event.timeStamp)?string('HH:mm:ss,SSS')}</span>
		<span class="category">${event.loggerName}</span>
		<a href="#d${event_index}" class="toggle">Show Details</a>
		<table id="d${event_index}" class="details">
			<tr>
				<th>Thread</th>
				<td>${event.threadName}</td>
			</tr>
			<#list event.properties?keys as key>
				<#if !key?starts_with("_")>
					<tr>
						<th>${key}</th>
						<td>${event.properties[key]?string}</td>
					</tr>
				</#if>
			</#list>
		</table>
		
		<tt class="message">${event.renderedMessage!?replace(',',',&#x200B;')?replace('/','&#x200B;/')?replace('\n', '<br/>')?replace('\\s(?=\\s)','&nbsp;','r')}</tt>
		
		<#if event.throwableStrRep?has_content>
			<a href="#s${event_index}" class="toggle">Show Stacktrace</a>
			<tt id="s${event_index}" class="stack">
				<#list event.throwableStrRep as line>${line?replace('\\s(?=\\s)','&nbsp;','r')}<br/></#list>
			</tt>
		</#if>
	</div>
</#list>

</body>
</html>