<li id="${item.id}" class="mapItem ${(element.compositeElement?default(false))?string('composite','single')}MapItem">
	<div class="mapItem">
		<label for="${element.id}">${item.label}<#if element.required && !element.compositeElement?default(false)>* </#if></label>
		<div class="itemElement">
			${element.render()} ${element.form.errors.renderErrors(element)}
		</div>
	</div>
</li>