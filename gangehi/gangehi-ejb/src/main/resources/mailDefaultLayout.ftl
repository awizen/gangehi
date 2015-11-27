<#macro mailLayout>
<html>
<body>

<table cellspacing="0" cellpadding="4px" border="0" width="600px">
<tbody>
<tr style="background: #6ba3f5 none repeat scroll 0 0;    box-shadow: 0 0 0 #fff inset, 0 -20px 20px -20px rgba(0, 0, 0, 0.75) inset;">

<td >
<img src="${imgAsBase64}" style="width: 281px;"/>
</td>
</tr>
<tr>
<td >
<br/>

	<#nested/>

<p> 
With kind regards,<br/>
Your Gangehi team
</p>

<hr color=#1176b9 align=left noshade size=4 width="100%"/>

<p>
<font color="grey">Copyright &copy; 2015 Gangehi Approvals, Alpha-Version</font>
</p>

</td>

</tr>
</tbody>
</table>


</body>
</html>
</#macro>