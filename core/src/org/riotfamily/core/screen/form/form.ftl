<@template.set stylesheets=[
	"style/form.css", 
	"style/form-custom.css",
	"style/command.css", 
	"style/command-custom.css"
] />
<@template.extend file="../screen.ftl">
			
	<@template.block name="main">
		<@riot.script src="/engine.js" />
		<@riot.script src="/util.js" />

		<@riot.script src="/interface/ListService.js" />
		<@riot.script src="riot/pager.js" />
		<@riot.script src="list.js" />
		<div id="form">${form}</div>
	</@template.block>
		
	<@template.block name="extra">
		<div class="box">
			<div class="commands">
				<a class="action enabled" href="javascript:save()"><span class="icon-and-label"><span class="icon saveButton"></span><span class="label"><@c.message "label.form.button.save">Save</@c.message></span></span></a>
				<div id="commands"></div>
			</div>
		</div>
	
		<script type="text/javascript" language="JavaScript">
			var form = $$('form')[0];

			riot.Resources.waitFor('focusElement', function() {
				focusElement(<#if focus??>$('${focus}')<#else>form</#if>);
			});
			
			if (document.addEventListener && typeof document.activeElement == 'undefined') {
			    document.addEventListener("focus", function(e) {
					if (e && e.target) {
				    	document.activeElement = e.target == document ? null : e.target;
					}
				}, true);
			} 
			
			<#--
			  - Submits the form by clicking on the submit button. 
			  - This function is invoked by the 'Save' button
			  - in the right-hand column.
			  -->
			function save(stayInForm) {
				if (stayInForm) {
					form.insert(new Element('input', {
						type: 'hidden', 
						name: 'focus', 
						value: document.activeElement}));
				}
				form.down('input.button-save').click();
			}
			
			<#--
			  - Registers a keypress listener on the given document. Note: 
			  - this function is also invoked by the setupcontent_callback of
			  - TinyMCE elements.
			  -->
			function registerKeyHandler(doc) {
				Event.observe(doc, 'keydown', function(e) {
				
					//we have to use code for KEY_CTRL(17) explicitly as it is not defined in Event
					if (e.keyCode == 17 || !e.ctrlKey)
						return;
					
					var key = String.fromCharCode(e.charCode ? e.charCode : e.keyCode).toUpperCase();
					if (key == 'S') {
						e.stop();					
						save(true);
					}
				});
			}
			
			registerKeyHandler(document);
			<#if context.objectId??>
				var list = new RiotList('${listStateKey}');
				list.renderFormCommands({objectId: '${context.objectId}'}, 'commands');
			</#if>
		</script>
		
		<#if childLinks??>
			<div class="box">
				<div class="links">
					<#list childLinks as child>
						<a class="screen" href="${c.url(child.url)}" style="${riot.iconStyle(child.icon!"brick")}">
							${child.title}
						</a>
					</#list>
				</div>
			</div>
		</#if>
		
	</@template.block>

</@template.extend>