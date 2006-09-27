<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/dwr/interface/JobUIService.js"></script>
<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/dwr/engine.js"></script>
<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/dwr/util.js"></script>
<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/prototype/prototype.js"></script>
<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/scriptaculous/effects.js"></script>
<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/riot-js/util.js"></script>
<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/style/tweak.js"></script>
<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/path.js"></script>
<link rel="stylesheet" href="${request.contextPath}${resourcePath}/style/common.css" type="text/css" />
<style type="text/css">
#name {
	margin-left: 4px;
}
#description {
	width: 600px;
	margin:20px 0 0 4px;
}

#control {
	position: relative;
	padding: 25px 4px;
	margin: 25px 0 40px 0;
	border-top: 1px solid #ebebeb;
	border-bottom: 1px solid #ebebeb;
}

#start, 
#cancel {
	width: 123px;
	height: 50px;
	margin: 0 10px 0 0;
	float: left;
	background-color: #cb0038;
	background-position: 10px 10px;
	background-repeat: no-repeat;
}
#start {
	background-image: url(/famab/riot/resources/6.2-2006-08-23/style/images/play.gif);
}
#start.running {
	background-image: url(/famab/riot/resources/6.2-2006-08-23/style/images/pause.gif);
}
#cancel {
	background-image: url(/famab/riot/resources/6.2-2006-08-23/style/images/cancel.gif);
}
#startFace,
#cancelFace {
	padding: 10px 0 0 40px;
	color: #fff;
	font-family: "Trebuchet MS", arial, sans-serif;
	font-size: 12px;
}

#progress {
	margin: 0 0 0 266px;
	position: relative;
	height: 50px;
}
#finished {
	position: absolute;
	top: 25px;
	left: 4px;
	display: none;
}
#eta {
	position: absolute;
	right: 0;
	font-family: arial;
	font-size: 11px;
}
#progress .bar {
	position: absolute;
	height: 4px;
	bottom: 0;
	left: 0;
	width: 100%;
	background-color: #ebebeb;
}
#percentage {
	position: absolute;
	bottom: 6px;
	text-align: right;
	color: #cb0038;
	font-family: arial;
	font-size: 14px;
	font-weight: bold;
}
#progressBar {
	height: 100%;
	width: 0px;
	background-color: #cb0038;
}

#logTitle {
	font-weight: bold;
	margin: 0 0 8px 4px;
}
#logPane {
	clear: left;
	background-color: #ebebeb;
	padding: 2px 2px 2px 4px;
}
#log {
	height: 200px;
	overflow: auto;
	margin: 0;
}
.prio2 {
	color: #cb0038;
}
#log.errorsOnly .prio1 {
	display: none;
}

#logFilter {
	margin:5px 3px;
}
#logFilter input {
	margin:1px 4px 0 0;
}
#logFilter label {
	font-size:11px;
}
</style>
</head>
<body onload="DWREngine.setReverseAjax(true)">
	<h1 id="name"></h1>
	<div id="description"></div>

	<div id="control">
		<div id="runnable">
			<div id="start"><div id="startFace"></div></div>
			<div id="cancel"><div id="cancelFace">Cancel</div></div>
		
			<div id="progress">
				<div id="eta"></div>
				<div id="percentage"></div>
				<div class="bar"><div id="progressBar"></div></div>
			</div>
		</div>
		<div id="finished">
			Job finished.
		</div>
	</div>
		
	<div id="logTitle">Log Messages</div>
	<div id="logPane">
		<div id="log"></div>
	</div>
	<div id="logFilter">
		<input type="checkbox" id="errorsOnly" onclick="Element.toggleClassName('log', 'errorsOnly')" /><label for="errorsOnly">Errors Only</label>
	</div>
	
<script type="text/javascript" language="JavaScript">

TweakStyle.roundElement('start', 'all');
TweakStyle.roundElement('cancel', 'all');
TweakStyle.roundElement('logPane', 'all');

var state;

function updateJob(job) {
	if (!state) {
		DWRUtil.setValue('name', job.name);
		DWRUtil.setValue('description', job.description);
		subPage(job.name); <#-- see path.js -->
	}
	if (job.estimatedTime) {
		if (job.state == 0) {
			DWRUtil.setValue('eta', 'This job will presumably take ' 
					+ job.estimatedTime + ' to complete');
		}
		else {
			DWRUtil.setValue('eta', job.estimatedTime + ' remaining');
		}
	}
	if (job.progress > 0) {
		$('percentage').style.width = job.progress + '%';
		$('percentage').innerHTML = job.progress + '%';
		$('progressBar').style.width = job.progress + '%';
	}
	if (job.state != state) {
		state = job.state;
		if (state == 1) {
			$('startFace').innerHTML = 'Pause';
			$('start').onclick = interruptJob;
			Element.addClassName('start', 'running');
		}
		else if (state == 0 || state == 2) {
			$('startFace').innerHTML = 'Start';
			$('start').onclick = startJob;
			Element.removeClassName('start', 'running');
		}
		else {
			$('runnable').style.visibility = 'hidden';
			$('finished').style.display = 'block';
			DWREngine.setReverseAjax(false);
		}
	}
}

function populateLog(entries) {
	var log = $('log');
	entries.each(function (entry) {
		log.appendChild(createLogElement(entry));
	});
}

function createLogElement(entry) {
	return Element.DIV({className: 'prio' + entry.priority}, entry.message);
}

function addLogEntry(entry) {
	var e = createLogElement(entry);
	Element.hide(e);	
	Element.prependChild('log', e);
	new Effect.Appear(e);
}

$('cancel').onclick = cancelJob;

function startJob() {
	JobUIService.executeJob(${jobId});
}

function interruptJob() {
	JobUIService.interruptJob(${jobId});
}

function cancelJob() {
	if (confirm('Do you really want to cancel this job?')) {
		JobUIService.cancelJob(${jobId});
	}
}

JobUIService.getLogEntries(${jobId}, populateLog);
JobUIService.getJobDetail(${jobId}, updateJob);
</script>
</body>
</html>