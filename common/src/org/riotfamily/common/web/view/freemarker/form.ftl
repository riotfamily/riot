<#assign command = "command" />

<#macro form command attributes...>
	<#assign command = command />
	<form${join(attributes)}>
		<#nested />
	</form>
</#macro>

<#function getStatus field="">
	<#if field?has_content>
		<#return springMacroRequestContext.getBindStatus(command + '.' + field) />
	<#else>
		<#return springMacroRequestContext.getBindStatus(command) />
	</#if>
</#function>

<#function hasErrors field="">
	<#local status = getStatus() />
	<#if !status.errors?exists>
		<#return false />
	</#if>
	<#if field?has_content>
		<#return status.errors.hasFieldErrors(field) />
	<#else>
		<#return status.errors.hasErrors() />
	</#if>
</#function>

<#macro listErrors field="" tag="" attributes...>
	<#local status = getStatus() />
	<#if field?has_content>
		<#local errors = status.errors.getFieldErrors(field) />
	<#else>
		<#local errors = status.errors.allErrors />
	</#if>
	<#if errors?has_content>
		<#if tag?has_content><${tag + join(attributes)}></#if>
		<#list errors as error>
			<#nested commonMacroHelper.getMessage(error), error />
		</#list>
		<#if tag?has_content></${tag}></#if>
	</#if>
</#macro>

<#macro label for field=for errorClass="error" code="" attributes...>
	<#local text><#nested /></#local>
	<#if !text?has_content>
		<#if !code?has_content>
			<#local code = command + '.' + field />
		</#if>
		<#local text = common.getMessage(code) />
	</#if>
	<#local attributes = addErrorClass(attributes, field, errorClass) />
	<label for="${for}"${join(attributes)}>${text}</label>
</#macro>

<#function getValue field>
	<#return getStatus(field).value?if_exists?string?html />
</#function>

<#macro input type field id=field errorClass="error" attributes...>
	<#if !attributes.class?exists>
		<#local attributes = attributes + {"class": type} />
	</#if>
	<#local attributes = addErrorClass(attributes, field, errorClass) />
    <input id="${id}" type="${type}" name="${field}" value="${getValue(field)}"${join(attributes)} />
</#macro>

<#macro textarea field id=field errorClass="error" attributes...>
	<#local attributes = addErrorClass(attributes, field, errorClass) />
    <textarea id="${id}" name="${field}"${join(attributes)}>${getValue(field)}</textarea>
</#macro>


<#macro select field options id=field errorClass="error" valueProperty="" labelProperty="" messagePrefix="" emptyValue="" emptyLabel="" addEmptyOption=false attributes...>
	<#local attributes = addErrorClass(attributes, field, errorClass) />
    <select id="${id}" name="${field}"${join(attributes)}>
    	<#if addEmptyOption || emptyLabel?has_content>
    		<@option emptyValue?html emptyLabel?html />
    	</#if>
        <@listOptions field options valueProperty labelProperty messagePrefix ; value, label, selected>
        	<@option value label selected />
        </@listOptions>
    </select>
</#macro>

<#macro checkbox field id=field errorClass="error" attributes...>
	<#local attributes = addErrorClass(attributes, field, errorClass) />
	<input type="checkbox" name="${field}" id="${id}" value="on"<@check getStatus(field).value?if_exists />${join(attributes)} />
	<input type="hidden" name="_${field}" value="on"/>
</#macro>

<#macro checkboxes field options>
	<@html.options field options ; value, label, checked, id>
		<input type="checkbox" name="${field}" id="${id}" value="${value}"<@check checked/> />
		<label for="${id}">${label}</label>
	</@html.options>
	<input type="hidden" name="_${field}" value="on" />
</#macro>

<#macro radioButtons field options>
	<@html.options field options ; value, label, checked, id>
		<input type="radio" name="${field}" id="${id}" value="${value}"<@check checked/> />
		<label for="${id}">${label}</label>
	</@html.options>
</#macro>
	
<#macro listOptions field options valueProperty="" labelProperty="" messagePrefix="">
	<#local value = getStatus(field).value?if_exists />
	<#if options?is_hash>
		<#list options?keys as option>
			<#if value?is_sequence>
	    		<#local selected = containsString(value, option) />
	    	<#else>
	    		<#local selected = (value == option) />
	    	</#if>
			<#nested option?html, options[option]?html, selected, field + '-' + option_index />
		</#list>
	<#else>
	    <#list options as option>
	    	<#if option?is_hash>
	    		<#if labelProperty?has_content>
	    			<#local optionLabel = option[labelProperty]?if_exists />
	    		<#else>
	    			<#local optionLabel = option?string />
	    		</#if>
	    		<#if valueProperty?has_content>
	    			<#local optionValue = option[valueProperty]?if_exists />
	    		<#else>
	    			<#local optionValue = option?string />
	    		</#if>
	    	<#else>
	    		<#local optionLabel = option?string />
	    		<#local optionValue = optionLabel />
	    	</#if>

	    	<#if messagePrefix?has_content>
	    		<#local optionLabel = common.getMessage(messagePrefix + optionLabel) />
	    	</#if>
	    	
	    	<#if value?is_sequence>
	    		<#local selected = containsString(value, optionValue) />
	    	<#else>
	    		<#local selected = (value?string == optionValue) />
	    	</#if>
	    	<#nested optionValue?html, optionLabel?html, selected, field + '-' + option_index />
	    </#list>
	</#if>
</#macro>

<#macro option value label=value selected=false attributes...>
	<option value="${value}"<#if selected> selected="selected"</#if>${join(attributes)}>${label}</option>
</#macro>

<#macro check checked><#if checked> checked="checked"</#if></#macro>

<#function containsString seq s>
	<#list seq as item>
		<#if item?string == s>
			<#return true />
		</#if>
	</#list>
	<#return false />
</#function>

<#function addErrorClass attributes field errorClass>
	<#if errorClass?has_content && hasErrors(field)>
		<#return attributes + {"class": (attributes.class?if_exists + ' ' + errorClass)?trim} />
	</#if>
	<#return attributes />
</#function>

<#function join attributes>
	<#local attrs = "" />
	<#if attributes?is_hash>
		<#list attributes?keys as attributeName>
			<#if attributes[attributeName]?has_content>
				<#local attrs = attrs + " " + attributeName + "=\"" + attributes[attributeName] + "\"" />
			</#if>
		</#list>
	</#if>
	<#return attrs />
</#function>