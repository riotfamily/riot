<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<@riot.script src="dwr/interface/JobUIService.js" />
<@riot.script src="dwr/engine.js" />
<@riot.script src="dwr/util.js" />
<@riot.script src="prototype/prototype.js" />
<@riot.script src="scriptaculous/effects.js" />
<@riot.script src="riot-js/util.js" />
<@riot.script src="style/tweak.js" />
<@riot.script src="path.js" />
<@riot.stylesheet href="style/common.css" />
<@riot.stylesheet href="style/job.css" />
</head>
<body id="job" onload="dwr.engine.setActiveReverseAjax(true)">
	<div id="body-wrapper">
		<div id="wrapper">	

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

subPage('<@spring.messageText "job.title." + type, type />'); <#-- see path.js -->
TweakStyle.roundElement('start', 'all');
TweakStyle.roundElement('cancel', 'all');
TweakStyle.roundElement('logPane', 'all');

var state;

function updateJob(job) {
	if (!state) {
		$('name').update(job.name || '');
		$('description').update(job.description || '<@spring.messageText "job.description.loading", "Please wait ..." />');
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
	if (job.description) {
		$('description').update(job.description);
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
			$('runnable').style.visibility = 'visible';
			$('startFace').update('Start');
			$('start').removeClassName('running').onclick = startJob;
		}
		else if (state == 3 || state == 4) {
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
	new Effect.Appear(e, {duration: 0.2});
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

		</div>
	</div>

</body>
</html>
