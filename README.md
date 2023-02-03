# OAuth2-Google / OIDC 


The demo of [yahoo-auth](https://github.com/Seo-4d696b75/yahoo-auth) is pretty nice. Article: [kibela](https://moneyforward.kibe.la/notes/154599)  
However, my phone number was banned by yahoo.   
**<font color='#FF450'>I AM INNOCENT!</font>**  
<img src="image/ImINNOCENT.png" width=200>  
So I write a demo of google api. Article: [kibela](https://moneyforward.kibe.la/notes/246633) 
The google api SDK wrap most of the functions. I unwrap some logic from SDK to make it easier to understand.  
Additionally, I add the OIDC. [reference](https://developers.google.com/identity/openid-connect/openid-connect)

## Preparation
> active api, set up app, get secret key... refer to [https://developers.google.com/people/v1/getting-started](https://developers.google.com/people/v1/getting-started)  
> api dashboard: [https://console.cloud.google.com/apis/dashboard](https://console.cloud.google.com/apis/dashboard)  
> If you want skip these progress that no matter with OAuth, just ask me to get credential file.

## Authorization
> We can build a request url to get Access Token.   
> AuthService.kt: getAccessCredential()  
> I build a `GoogleAuthorizationCodeRequestUrl` object
> We can check the url text from `GoogleAuthorizationCodeRequestUrl` object after build, it looks like this:
```
https://accounts.google.com/o/oauth2/auth?access_type=offline
    &client_id=${}
    &redirect_uri=http://localhost:8080/auth/goopeople
    &response_type=code
    &scope=https://www.googleapis.com/auth/userinfo.profile
```
OIDC:   
*`state` should include the value of the anti-forgery unique session token, as well as any other information needed to recover the context when the user returns to your application, e.g., the starting URL.*
```
https://accounts.google.com/o/oauth2/auth?access_type=offline
    &client_id=${}
    &redirect_uri=http://localhost:8080/auth/goopeopleOIDC&response_type=code
    &scope=https://www.googleapis.com/auth/userinfo.profile%20openid
    &state=demo_state
```
> Then we redirect to the url above⬆️   
> Then User need to grant to our app  
> <img src="image/login.png" width=200>  
> After grant, the response looks like:
```
http://localhost:8080/auth/goopeople?code=****************
    &scope=profile+https://www.googleapis.com/auth/userinfo.profile
```
OIDC:    
We need to check the state, to ensure that the user, not a malicious script, is making the request.  
I check it in controller.  
```
http://localhost:8080/auth/goopeopleOIDC?state=demo_state
    &code=****************
    &scope=profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid
    &authuser=1&prompt=consent
```

# Access token
> AuthService.kt: accessGooglePeople()  
> Build a `GoogleAuthorizationCodeTokenRequest`, we can check the request at `com.google.api.client.auth.oauth2.TokenRequest.executeUnparsed(TokenRequest.java:304)`  
> Make request by SDK. The url looks may like
```
https://oauth2.googleapis.com/token?code=***
    &grant_type=authorization_code
    &redirect_uri=http://localhost:8080/auth/goopeople
    &scope=https://www.googleapis.com/auth/userinfo.profile
```
> Response: 
```
{
     "access_token": "...",
     "expires_in": 3599,
     "refresh_token": "...",
     "scope": "https://www.googleapis.com/auth/userinfo.profile",
     "token_type": "Bearer"
 }
```
> OIDC:  
```
{
	"access_token": "...",
	"expires_in": 3599,
	"id_token": ${Base64},
	"refresh_token": "...",
	"scope": "https://www.googleapis.com/auth/userinfo.profile openid",
	"token_type": "Bearer"
}

```
# Access data
> Then send request to endpoint `https://people.googleapis.com/`.  
> Request URL:
```
https://people.googleapis.com/v1/people/me?personFields=names
    &access_token=***
```
> I only get name information.   
```
{
  "resourceName": "people/12345678",
  "etag": "%EgUBAi43PRoEBw==",
  "names": [
    {
      "metadata": {
        "primary": true,
        "source": {
          "type": "PROFILE",
          "id": "12345678"
        },
        "sourcePrimary": true
      },
      "displayName": "Zhen Shao",
      "familyName": "Shao",
      "givenName": "Zhen",
      "displayNameLastFirst": "Shao, Zhen",
      "unstructuredName": "Zhen Shao"
    }
  ]
}
```
OIDC:  
ID token decode:  AuthService.base64ToJson
documentation: [https://developers.google.com/identity/openid-connect/openid-connect#obtainuserinfo](https://developers.google.com/identity/openid-connect/openid-connect#obtainuserinfo)

## Exception
> If the exception `PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target` occurs.  
> First disable the netskope client  
> <img src="image/netskope.png" width=100>  
> If it makes nonsense, add the SSL certificate:
```
$JAVA_HOME/bin/keytool -import -alias ${name} 
    -keystore $JAVA_HOME/lib/security/cacerts -file upload.video.google.com.cer
```
> `upload.video.google.com.cer` in the resources folder
