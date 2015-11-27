<#import "mailDefaultLayout.ftl" as layout>
<@layout.mailLayout>

Dear ${toName},<br/>
<p> 
you have just requested a password reset. 
</p>
 
<p>
Please click the following link to go to the reset screen: <a href="${verificationUrl}">Reset Password</a>
</p>

<p>
Please do not respond to this email. 
</p>

  
</@layout.mailLayout>
