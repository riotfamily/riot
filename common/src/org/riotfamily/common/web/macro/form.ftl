<#---
  - Alternative to Spring's form macros.
  - @namespace form
  - @requires c 
 -->
 
<#---
  - Renders a form tag for the command object with the specified name.
  -
  - @param command Name of the command object.
  - @param attributes All additional attributes that will be added to the form tag.
  -->
<#macro form command="command" attributes...>
	${formMacroHelper.start(command, springMacroRequestContext)!}
	<form${c.joinAttributes(attributes)}>
		<#nested />
	</form>
	${formMacroHelper.end()!}
</#macro>

<#---
  - Returns whether the form has any errors.
  -->
<#function hasErrors form=formMacroHelper.command>
	<#return formMacroHelper.hasErrors(form, springMacroRequestContext) />
</#function>

<#---
  - Returns whether the form has any global errors.
  -->
<#function hasGlobalErrors>
	<#return formMacroHelper.hasGlobalErrors() />
</#function>

<#---
  - Returns whether the field has any errors.
  -->
<#function hasFieldErrors field>
	<#return formMacroHelper.hasFieldErrors(field) />
</#function>

<#--- 
  - Loops over the errors of the specified field. In case no field is given, 
  - the macro will loop over all errors.
  - Use this macro with two loop variables, to expose the error message
  - and the message code to the nested content.
  -->
<#macro listErrors field="" tag="" attributes...>
	<#if field?has_content>
		<#local errors = formMacroHelper.getFieldErrors(field) />
	<#else>
		<#local errors = formMacroHelper.getAllErrors() />
	</#if>
	<#if errors?has_content>
		<#if tag?has_content><${tag + c.joinAttributes(attributes)}></#if>
		<#list errors as error>
			<#nested commonMacroHelper.getMessage(error), error />
		</#list>
		<#if tag?has_content></${tag}></#if>
	</#if>
</#macro>

<#--- 
  - Loops over the errors of the specified field. In case no field is given, 
  - the macro will loop over all field errors.
  - Use this macro with two loop variables, to expose the error message
  - and the message code to the nested content.
  -->
<#macro listFieldErrors field="" tag="" attributes...>
	<#local errors = formMacroHelper.getFieldErrors(field) />
	<#if errors?has_content>
		<#if tag?has_content><${tag + c.joinAttributes(attributes)}></#if>
		<#list errors as error>
			<#nested commonMacroHelper.getMessage(error), error />
		</#list>
		<#if tag?has_content></${tag}></#if>
	</#if>
</#macro>

<#--- 
  - Loops over the global errors.
  - Use this macro with two loop variables, to expose the error message
  - and the message code to the nested content.
  -->
<#macro listGlobalErrors tag="" attributes...>
	<#local errors = formMacroHelper.getGlobalErrors() />
	<#if errors?has_content>
		<#if tag?has_content><${tag + c.joinAttributes(attributes)}></#if>
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
<#macro label for field="" errorClass="error" code="" attributes...>
	<#compress>
		<#if !field?has_content>
			<#local field = for />
		</#if>
		<#local text><#nested /></#local>
		<#if !text?has_content>
			<#if !code?has_content>
				<#local code = formMacroHelper.command + '.' + field />
			</#if>
			<#local text = c.getMessage(code) />
		</#if>
		<#local attributes = addErrorClass(attributes, field, errorClass) />
		<label for="${for}"${c.joinAttributes(attributes)}>${text}</label>
	</#compress>
</#macro>

<#function getDisplayValue field>
	<#return formMacroHelper.getDisplayValue(field)?html />
</#function>

<#---
  - Renders an input element for the specified field.
  -->
<#macro input type field id=field errorClass="error" attributes...>
	<#compress>
		<#if !attributes.class?exists>
			<#local attributes = attributes + {"class": type} />
		</#if>
		<#local attributes = addErrorClass(attributes, field, errorClass) />
	    <input id="${id}" type="${type}" name="${field}" value="${getDisplayValue(field)}"${c.joinAttributes(attributes)} />
	</#compress>
</#macro>

<#---
  - Renders a textarea for the specified field.
  -->
<#macro textarea field id=field errorClass="error" attributes...>
	<#local attributes = addErrorClass(attributes, field, errorClass) />
    <textarea id="${id}" name="${field}"${c.joinAttributes(attributes)}>${getDisplayValue(field)}</textarea>
</#macro>

<#---
  - Renders a select element. See also #listOptions.
  -
  - @param field The field name
  - @param options An iterable containing the possible options
  - @param id The element's CSS id.
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
	
	<#compress>
		<#local attributes = addErrorClass(attributes, field, errorClass) />
	    <select id="${id}" name="${field}"${c.joinAttributes(attributes)}>
	    	<#if addEmptyOption || emptyLabel?has_content>
	    		<@option emptyValue emptyLabel />
	    	</#if>
	        <@listOptions field options valueProperty labelProperty messagePrefix ; value, label, selected>
	        	<@option value label selected />
	        </@listOptions>
	    </select>
	</#compress>
</#macro>

<#---
  - Renders a checkbox. 
  -->
<#macro checkbox field id=field errorClass="error" value="true" attributes...>
	<#compress>
		<#local attributes = addErrorClass(attributes, field, errorClass) />
		<input type="checkbox" name="${field}" id="${id}" value="${value?html}"<@check formMacroHelper.isSelected(field, value) />${c.joinAttributes(attributes)} />
		<input type="hidden" name="_${field}" value="on"/>
	</#compress>
</#macro>

<#---
  - Renders a list of checkboxes for the given options. See also #listOptions.
  -->
<#macro checkboxes field options labelFirst=false valueProperty="" labelProperty="" messagePrefix="" errorClass="error" attributes...>
	<#compress>
		<#local attributes = addErrorClass(attributes, field, errorClass) />
		<@listOptions field options valueProperty labelProperty messagePrefix ; value, label, checked, id>
			<#if labelFirst>
				<label for="${id}"${c.joinAttributes(attributes)}>${label}</label>
				<input type="checkbox" name="${field}" id="${id}" value="${value?html}"<@check checked/>${c.joinAttributes(attributes)} />		
			<#else>
				<input type="checkbox" name="${field}" id="${id}" value="${value?html}"<@check checked/>${c.joinAttributes(attributes)} />
				<label for="${id}"${c.joinAttributes(attributes)}>${label}</label>
			</#if>
		</@listOptions>
		<input type="hidden" name="_${field}" value="on" />
	</#compress>
</#macro>

<#---
  - Renders a list of radio buttons for the given options. See also #listOptions.
  -->
<#macro radioButtons field options labelFirst=false valueProperty="" labelProperty="" messagePrefix="" errorClass="error" attributes...>
	<#compress>
		<#local attributes = addErrorClass(attributes, field, errorClass) />
		<@listOptions field options valueProperty labelProperty messagePrefix ; value, label, checked, id>
			<#if labelFirst>
				<label for="${id}"${c.joinAttributes(attributes)}>${label}</label>
				<input type="radio" name="${field}" id="${id}" value="${value?html}"<@check checked/>${c.joinAttributes(attributes)} />
			<#else>			
				<input type="radio" name="${field}" id="${id}" value="${value?html}"<@check checked/>${c.joinAttributes(attributes)} />
				<label for="${id}"${c.joinAttributes(attributes)}>${label}</label>
			</#if>		
		</@listOptions>
	</#compress>
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
  - ... in which case the labelProperty and valueProperty parameters can be used
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
	<#if options?is_hash>
		<#list options?keys as option>			
			<#local selected = formMacroHelper.isSelected(field, option, valueProperty) />
			<#nested option, options[option], selected, field + '-' + option_index />
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
	    			<#if optionValue?? && !optionValue?is_string>
	    				<#local optionValue = optionValue?string /> 
	    			</#if>	    			
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
	    	
	    	<#local selected = formMacroHelper.isSelected(field, optionValue, valueProperty) />
	    	<#nested optionValue, optionLabel, selected, field + '-' + option_index />
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
	<#compress>
		<option value="${value?html}"<#if selected> selected="selected"</#if>${c.joinAttributes(attributes)}>${label}</option>
	</#compress>
</#macro>

<#macro check checked><#if checked> checked="checked"</#if></#macro>

<#---
  - Returns whether a sequence contains the given String.
  -
  - @internal
  -->
<#function containsString seq s>
	<#list seq as item>
		<#if item?string == s>
			<#return true />
		</#if>
	</#list>
	<#return false />
</#function>

<#---
  - Adds the specified errorClass to the attributes hash if the given field
  - has han error.
  -
  - @internal
  -->
<#function addErrorClass attributes field errorClass>
	<#if errorClass?has_content && hasFieldErrors(field)>
		<#return attributes + {"class": (attributes.class?if_exists + ' ' + errorClass)?trim} />
	</#if>
	<#return attributes />
</#function>
