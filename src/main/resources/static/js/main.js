$(document).ready(function(){
	
 
	
$("#loginbutton").click(function(){
  window.location.replace("https://qa-id.uaepass.ae/trustedx-authserver/oauth/main-as?redirect_uri=https://uaepassuat.afexapp.com&client_id=alfardan_web_stage&response_type=code&state=QnmvYTQZpCYjOcjn&scope=urn:uae:digitalid:profile:general&acr_values=urn:safelayer:tws:policies:authentication:level:low&ui_locales=en");
});

$("#gotohomepage").click(function(){
  window.location.replace("https://alfardanexchange.com/");
});


});