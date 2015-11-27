<#import "mailDefaultLayout.ftl" as layout>
<@layout.mailLayout>

Dear ${toName},<br/>
<p> 
the request for approval you were involved in has been <b>aborted</b> by the originator ${originator}. 
</p>
 
<p>
Please visit 
<a href="${taskUrl}">Gangehi Approval System</a>
 to view the details of the aborted approval: "${taskSubject}"
</p>

<#if showReplyWarning == "true">
	<p>
	When responding to this email you will directly contact the originator of the approval process. 
	</p>
</#if>
  
</@layout.mailLayout>
