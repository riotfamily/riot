<form method="get">
	<input type="text" class="text"	name="search" value="${result.originalQuery?if_exists}"	/>					
	<input type="submit" class="submit" value="Search" />
</form>

<#if result.items?has_content>
	<p>
		${springMacroRequestContext.getMessage("riot.search.resultCount", 
				[result.originalQuery, result.totalHitCount],
				"Your search for '{0}' returned {1} results:")?html}
	</p>
	<ul class="search-result">
		<#list result.items as item>
			<li>
				<a href="${item.link}">
					<h3>${item.title?default(item.link)}</h3>
					<p>${item.description?if_exists}</p>
				</a>
			</li>
		</#list>
	</ul>
	<#if pager.pages gt 1>
		<#if pager.prevPage?exists>
			<a class="prev-page" href="${pager.prevPage.link}">&lt;&lt;</a>
		</#if>
		
		<#if pager.firstPage?exists>
			<a class="page" href="${pager.firstPage.link}">1</a>
			<#if pager.gapToFirstPage> <span class="gap">...</span> </#if>
		</#if>
		
		<#list pager.prevPages as page>
			<a class="page" href="${page.link}">${page.number}</a>
		</#list>
		
		<span class="current-page">${pager.currentPage}</span>
		
		<#list pager.nextPages as page>
			<a class="page" href="${page.link}">${page.number}</a>
		</#list>
		
		<#if pager.lastPage?exists>
			<#if pager.gapToLastPage> <span class="gap">...</span> </#if>
			<a class="page" href="${pager.lastPage.link}">${pager.lastPage.number}</a>
		</#if>
		
		<#if pager.nextPage?exists>
			<a class="next-page" href="${pager.nextPage.link}">&gt;&gt;</a>
		</#if>
	</#if>
<#elseif result.originalQuery?has_content>
	<p>
		${springMacroRequestContext.getMessage("riot.search.noResults", 
			[result.originalQuery], "Your search for '{0}' did not return any results.")?html}
	</p>
</#if>
