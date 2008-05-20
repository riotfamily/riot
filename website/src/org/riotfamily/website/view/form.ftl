<#---
  - Alternative to Spring's form macros.
  - @namespace form 
 -->
 
<#---
  - @internal
  -->
<#assign command = "command" />

<#---
  - Renders a form tag for the command object with the specified name.
  - The macro also sets the command variable which is required by the
  - getStatus() function (which in turn is used internally in many places).
  -
  - @param command Name of the command object. Defaults to 'command'.
  - @param Additional attributes that will be added to the form tag.
  -->
<#macro form command attributes...>
	<#assign command = command />
	<form${join(attributes)}>
		<#nested />
	</form>
</#macro>

<#---
  - Returns whether a field or the whole form contains an error.
  -->
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

<#--- 
  - Loops over the errors of the specified field. In case no fieldname is given, 
  - the macro will loop over all errors.
  - Use this macro with two loop variables, which will make the error message
  - and the message code available to the nested content.
  -->
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

<#---
  - Renders a label for a field. If no nested content is present, the macro
  - will look up the message with the given code, or 
  - &lt;command&gt;.&lt;field&gt; if no code is explicitly specified.
  - If the field has a validation error, a special CSS class will be added to
  - the label tag (default is 'error'). 
  -->
<#macro label for field=for errorClass="error" code="" attributes...>
	<#local text><#nested /></#local>
	<#if !text?has_content>
		<#if !code?has_content>
			<#local code = command + '.' + field />
		</#if>
		<#local text = c.getMessage(code) />
	</#if>
	<#local attributes = addErrorClass(attributes, field, errorClass) />
	<label for="${for}"${join(attributes)}>${text}</label>
</#macro>

<#function getValue field>
	<#return getStatus(field).value?if_exists?string?html />
</#function>

<#---
  - Renders an input element for the specified field.
  -->
<#macro input type field id=field errorClass="error" attributes...>
	<#if !attributes.class?exists>
		<#local attributes = attributes + {"class": type} />
	</#if>
	<#local attributes = addErrorClass(attributes, field, errorClass) />
    <input id="${id}" type="${type}" name="${field}" value="${getValue(field)}"${join(attributes)} />
</#macro>

<#---
  - Renders a textarea for the specified field.
  -->
<#macro textarea field id=field errorClass="error" attributes...>
	<#local attributes = addErrorClass(attributes, field, errorClass) />
    <textarea id="${id}" name="${field}"${join(attributes)}>${getValue(field)}</textarea>
</#macro>

<#---
  - Renders a select element. See also #listOptions.
  -
  - @param field The field name
  - @param options An iterable containing the possible options
  - @param id The element's CSS id. Defaults to the field name.
  - @param errorClass The CSS class to assign in case of a validation error.
  - @param valueProperty Name of the property that contains the option's value.
  - @param labelProperty Name of the property that contains the option's label.
  - @param mesagePrefix Prefix to add to message codes when looking up option labels.
  - @param addEmptyOption Whether an empty option should be added.
  - @param emptyValue The value to use for the empty option.
  - @param emptyLabel The label to use for the empty option.
  - @param attributes Additional attributes to be added to the select element. 
  -->
<#macro select field options id=field errorClass="error" valueProperty="" 
	labelProperty="" messagePrefix="" addEmptyOption=false emptyValue="" emptyLabel="" attributes...>
	
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

<#---
  - Renders a checkbox. 
  -->
<#macro checkbox field id=field errorClass="error" value="on" attributes...>
	<#local attributes = addErrorClass(attributes, field, errorClass) />
	<input type="checkbox" name="${field}" id="${id}" value="${value}"<@check getStatus(field).value?if_exists />${join(attributes)} />
	<input type="hidden" name="_${field}" value="on"/>
</#macro>

<#---
  - Renders a list of checkboxes for the given options. See also #listOptions.
  -->
<#macro checkboxes field options>
	<@listOptions field options ; value, label, checked, id>
		<input type="checkbox" name="${field}" id="${id}" value="${value}"<@check checked/> />
		<label for="${id}">${label}</label>
	</@listOptions>
	<input type="hidden" name="_${field}" value="on" />
</#macro>

<#---
  - Renders a list of radio buttons for the given options. See also #listOptions.
  -->
<#macro radioButtons field options>
	<@listOptions field options ; value, label, checked, id>
		<input type="radio" name="${field}" id="${id}" value="${value}"<@check checked/> />
		<label for="${id}">${label}</label>
	</@listOptions>
</#macro>

<#---
  - Macro that loops over an iterable containing options and evaluates the 
  - nested content for each option, passing several useful loop variables.
  -
  - If the given options are a hash, the macro will use the keys as values
  - and the associated values as label, e.g. {"23": "Twentythree", "42": "Fourtytwo"}.
  -
  - Alternatively a list of hashes may be used as options, e.g.
  - [{"label": "Twentythree", "value": 23}, {"label": "Fourtytwo", "value": 42}]
  - ... in which case the labelProperty and valueProperty paramters can be used
  - to specify the keys under which the label/value can be found in the hashes.
  -
  - You can also pass in a list of arbitrary objects an either use the values
  - as labels or lookup the labels from a MessageSource.
  -
  - @param field The field name, used to obtain the selected value(s).
  - @param options A list or hash containing the options.
  - @param valueProperty The property to use as option value.
  - @param labelProperty The property to use as option label.
  - @param messagePrefix A prefix added to the massage codes.
  -->	
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
	    		<#local optionLabel = c.getMessage(messagePrefix + optionLabel) />
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

<#---
  - Macro that renders an option tag.
  -
  - @param value The value for the value attribute.
  - @param label The label to render. Defaults to the value.
  - @param selected A boolean indicating whether the option is selected.
  - @param attributes Additional attributes to be added to the option tag. 
  --> 
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

<#function getStatus field="">
	<#if field?has_content>
		<#return springMacroRequestContext.getBindStatus(command + '.' + field) />
	<#else>
		<#return springMacroRequestContext.getBindStatus(command) />
	</#if>
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