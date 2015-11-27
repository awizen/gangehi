<#import "mailDefaultLayout.ftl" as layout>
<@layout.mailLayout>

Dear ${toName},<br/>
<p> 
your registration to Gangehi Approval Application is almost completed. 
</p>
 
<p>
Please click the following link to verify your email: <a href="${verificationUrl}">Email verification</a>
</p>

<p>
Please do not respond to this email. 
</p>

  
</@layout.mailLayout>


