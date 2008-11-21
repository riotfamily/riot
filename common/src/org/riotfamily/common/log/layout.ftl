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

a.block {
	display: block;
	text-decoration: none;
	color: #000;
	-moz-outline: none;
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
	.block:focus .stack,
	.block:focus .details {
		display: block;
	}
	.block:focus .toggle {
		display: none;
	}
</style>
</head>
<body>

<#list events as event>
	<div class="entry ${event.level?lower_case}">
		<a href="#show" class="block">
			<span class="time">${helper.toDate(event.timeStamp)?string('HH:mm:ss,SSS')}</span>
			<span class="category">${event.loggerName}</span>
			<span class="toggle">Show Details</span>
			<table class="details">
				<tr>
					<th>Thread</th>
					<td>${event.threadName}</td>
				</tr>
				<#list event.properties?keys as key>
					<tr>
						<th>${key}</th>
						<td>${event.properties[key]}</td>
					</tr>
				</#list>
			</table>
		</a>
		<tt class="message">${event.renderedMessage?replace(',',',&#x200B;')?replace('/','&#x200B;/')?replace('\n', '<br/>')?replace('\\s(?=\\s)','&nbsp;','r')}</tt>
		<#if event.ThrowableStrRep?has_content>
			<a href="#show" class="block">
				<span class="toggle">Show Stacktrace</span>
				<tt class="stack">
					<#list event.ThrowableStrRep as line>${line?replace('\\s(?=\\s)','&nbsp;','r')}<br/></#list>
				</tt>
			</a>
		</#if>
	</div>
</#list>

</body>
</html>