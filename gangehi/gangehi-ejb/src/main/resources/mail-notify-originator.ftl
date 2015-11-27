<#import "mailDefaultLayout.ftl" as layout>
<@layout.mailLayout>

Dear ${toName},<br/>
<p> 
your request for approval has been rejected by ${rejector}. 
</p>
 
<p>
Please visit 
<a href="${taskUrl}">Gangehi Approval System</a>
 to adjust your approval task: "${taskSubject}"
</p>

<#if showReplyWarning == "true">
	<p>
	When responding to this email you will directly contact the person which has rejected the approval (${rejector}). 
	</p>
</#if>

  
</@layout.mailLayout>