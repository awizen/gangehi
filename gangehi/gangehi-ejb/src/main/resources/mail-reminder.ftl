<#import "mailDefaultLayout.ftl" as layout>
<@layout.mailLayout>

Dear ${toName},<br/>
<p> 
there are open tasks in the the Gangehi Approval System waiting for your approval. 
</p>

<p>
Please visit 
<a href="${taskUrl}">Gangehi Approval System</a>
 to view and complete the approval task: "${taskSubject}"
</p>

</@layout.mailLayout>

