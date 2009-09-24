<#---
  - Macros to define and overwrite template blocks.
  - @namespace template
  -->

<#assign root = templateMacroHelper.rootDirective />

<#---
  - Variable that contains a macro to define a child template that extends
  - another template. The value of this variable is a TemplateDirectiveModel
  - implemented in Java.
  - <pre>
  - &lt;@template.extend file="parent.ftl"&gt;
  -   &lt;@template.block name="content"&gt;
  -      Text that overwrites the "content" block defined in parent.ftl
  -   &lt;/@template.block&gt;
  - &lt;/@template.extend&gt;
  - </pre> 
  -->
<#assign extend = templateMacroHelper.extendDirective />

<#---
  - Defines a template block that can be overwritten by child templates.
  - @see <a href="#extend>&lt;@extend file /&gt;</a> 
  -->
<#assign block = templateMacroHelper.blockDirective />

<#---
  - Function that checks whether a block with the given name exists, i.e. has
  - been defined by a child template.
  -->
<#function blockExists name>
	<#return templateMacroHelper.blockExists(name) />
</#function>

<#---
  - Hash that contains variables set by child templates.
  - @see <a href="#set">&lt;@set values... /&gt;</a>
  -->
<#assign vars = {} />

<#---
  - Macro that sets one or more variables in the vars hash, unless the hash 
  - already contains a value with the same name. This way you can define 
  - variables in a child template that don't get overwritten by vars set in
  - the parent template.
  - <p>
  - <strong>Example:</strong> In <tt>child.ftl</tt> (which 
  - extends <tt>parent.ftl</tt>) set <code>foo</code> to <code>"bar"</code>:
  - </p>
  - <pre>&lt;@template.set foo="bar" /&gt;</pre>
  - <p>
  - In <tt>parent.ftl</tt> (which extends <tt>root.ftl</tt>) set 
  - <code>foo</code> to <code>"baz"</code>:
  - </p>
  - <pre>&lt;@template.set foo="baz" /&gt;</pre>
  - <p>
  - In <tt>root.ftl</tt> access <code>foo</code>:
  - </p>
  - <pre>${template.vars.foo} ==&gt; "bar"</pre> 
  -->
<#macro set values...>
	<#list values?keys as name>
		<#if !vars[name]??><#assign vars = vars + {name: values[name] } /></#if>	
	</#list>
</#macro>