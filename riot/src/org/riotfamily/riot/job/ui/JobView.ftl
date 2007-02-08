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
<link rel="stylesheet" href="${request.contextPath}${resourcePath}/style/job.css" type="text/css" />
</head>
<body onload="dwr.engine.setActiveReverseAjax(true)">
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
		<input type="checkbox" id="errorsOnly" onclick="RElement.toggleClassName('log', 'errorsOnly')" /><label for="errorsOnly">Errors Only</label>
	</div>
	
<script type="text/javascript" language="JavaScript">

TweakStyle.roundElement('start', 'all');
TweakStyle.roundElement('cancel', 'all');
TweakStyle.roundElement('logPane', 'all');

var state;

function updateJob(job) {
	if (!state) {
		$('name').update(job.name || '');
		$('description').update(job.description || '');
		subPage(job.name); <#-- see path.js -->
	}
	if (job.estimatedTime) {
		if (job.state == 0) {
			$('eta').update('This job will presumably take ' 
					+ job.estimatedTime + ' to complete');
		}
		else {
			$('eta').update(job.estimatedTime + ' remaining');
		}
	}
	if (job.progress > 0) {
		$('percentage').style.width = job.progress + '%';
		$('percentage').update(job.progress + '%');
		$('progressBar').style.width = job.progress + '%';
	}
	if (job.state != state) {
		state = job.state;
		if (state == 1) {
			$('startFace').update('Pause');
			$('start').addClassName('running').onclick = interruptJob;
		}
		else if (state == 0 || state == 2) {
			$('startFace').update('Start');
			$('start').removeClassName('running').onclick = startJob;
		}
		else {
			$('runnable').style.visibility = 'hidden';
			$('finished').style.display = 'block';
			dwr.engine.setActiveReverseAjax(false);
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
	return RBuilder.node('div', {className: 'prio' + entry.priority}, entry.message);
}

function addLogEntry(entry) {
	var e = createLogElement(entry).hide(e);	
	RElement.prependChild('log', e);
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