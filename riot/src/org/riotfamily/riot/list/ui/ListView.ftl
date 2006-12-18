<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<link rel="stylesheet" href="${request.contextPath}${resourcePath}/style/list.css" type="text/css" />
		<#list customStyleSheets as item>
			<link rel="stylesheet" href="${request.contextPath}${resourcePath}/${item}" type="text/css" />
		</#list>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/prototype/prototype.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/list.js"></script>		
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/path.js"></script>		
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/style/tweak.js"></script>		
		<script type="text/javascript" language="JavaScript">
			updatePath('${list.editorId}', null, '${list.parentId?if_exists}');
		</script>		
	</head>
	<body onload="TweakStyle.list()">
		<div id="wrapper">
			<div class="main">
				<table id="list" class="${list.cssClass}">
					<thead>
						<tr>
							<#list list.headings as heading>
								<th class="${heading.cssClass}">
									${heading.data?if_exists}
								</th>
							</#list>
						</tr>
					</thead>
					<tbody>
						<#list list.rows as row>
							<tr id="object-${row.objectId}"<#if row.cssClass?exists> class="${row.cssClass}"</#if>>
								<#list row.cells as cell>
									<td class="${cell.cssClass}">${cell.data}</td>
								</#list>
							</tr>
						</#list>
					</tbody>
					<#if list.pager?exists>
						<tfoot>
							<tr>
								<td colspan="${list.colCount}" class="pager">
									<#if list.pager.prevPage?exists>
										<a href="${list.pager.prevPage.link}">&lt;&lt;</a>
									</#if>
								
									<#if list.pager.firstPage?exists>
										<a class="page" href="${list.pager.firstPage.link}">1</a>
										<#if list.pager.gapToFirstPage> <span class="gap">...</span> </#if>
									</#if>
								
									<#list list.pager.prevPages as page>
										<a class="page" href="${page.link}">${page.number}</a>
									</#list>
								
									<span class="currentPage">${list.pager.currentPage}</span>
								
									<#list list.pager.nextPages as page>
										<a class="page" href="${page.link}">${page.number}</a>
									</#list>
								
									<#if list.pager.lastPage?exists>
										<#if list.pager.gapToLastPage> <span class="gap">...</span> </#if>
										<a class="page" href="${list.pager.lastPage.link}">${list.pager.lastPage.number}</a>
									</#if>
								
									<#if list.pager.nextPage?exists>
										<a href="${list.pager.nextPage.link}">&gt;&gt;</a>
									</#if>
								</td>
							</tr>
						</tfoot>
					</#if>	
				</table>
			</div>		
		</div>
		<div class="extra">
			
			<#if list.commands?has_content>
				<div id="commands" class="box">
					<div class="title">
						<div class="icon"></div>
						<span><@spring.messageText "label.commands", "Commands" /></span>
					</div>
					<div class="list">
						<#list list.commands as command>
							${command}
						</#list>
					</div>
				</div>
			</#if>
			
			<#if filterForm?exists>
				<div id="filter" class="box">
					<div class="title">
						<div class="icon"></div>
						<span><@spring.messageText "label.list.filter", "Filter" /></span>
					</div>
					${filterForm}
				</div>
			</#if>
			
		</div>
		<script type="text/javascript" language="JavaScript">
			initList('list', '${list.defaultCommandId?if_exists}');
		</script>
	</body>
</html>