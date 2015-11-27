<#import "mailDefaultLayout.ftl" as layout>
<@layout.mailLayout>

Dear ${toName},<br/>
<p> 
you have received a request for approval from ${originator} generated in the Gangehi Approval System. 
</p>

<p>
Please visit 
<a href="${taskUrl}">Gangehi Approval System</a>
 to view and complete the approval task: "${taskSubject}"
</p>

<#if showReplyWarning == "true">
	<p>
	When responding to this email you will contact the originator (${originator}) directly. 
	</p>
</#if>
  
</@layout.mailLayout>

