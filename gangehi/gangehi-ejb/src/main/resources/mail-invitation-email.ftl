<#import "mailDefaultLayout.ftl" as layout>
<@layout.mailLayout>

Dear ${toName},<br/>
<p> 
the user <b>${originator}</b> invites you to participate on Gangehi Approval System processes. 
</p>
 
<p>
Please click the following link to verify your email: <a href="${verificationUrl}">Email verification</a>
</p>

<p>
After the email verification you will have to start the "Password Recovery" to generate your new secure password. You can start the "Password Recovery" on the login page of the Gangehi Approval System.
</p>

<p>
Please contact the user ${originator} if you have any further questions. When responding to this email you will contact ${originator} directly. 
</p>

  
</@layout.mailLayout>