# OAuth2-Google


The demo of [yahoo-auth](https://github.com/Seo-4d696b75/yahoo-auth) is pretty nice. Article: [kibela](https://moneyforward.kibe.la/notes/154599)  
However, my phone number was banned by yahoo.   
**<font color='#FF450'>I AM INNOCENT!</font>**  
<img src="image/ImINNOCENT.png" width=200>  
So I write a demo of google api.  
The google api SDK wrap most of the functions. I unwrap some logic from SDK to make it easier to understand.  

## Preparation
> active api, set up app, get secret key... refer to [https://developers.google.com/people/v1/getting-started](https://developers.google.com/people/v1/getting-started)  
> api dashboard: [https://console.cloud.google.com/apis/dashboard](https://console.cloud.google.com/apis/dashboard)  
> If you want skip these progress that no matter with OAuth, just ask me to get credential file.

## Authorization
> We can build a request url to get Access Token.   
> AuthService.kt#Line 48: getAccessCredential()  
> I build a `GoogleAuthorizationCodeRequestUrl` object
> We can check the url text from `GoogleAuthorizationCodeRequestUrl` object after build, it looks like this:
```
https://accounts.google.com/o/oauth2/auth?access_type=offline
    &client_id=${}
    &redirect_uri=http://localhost:8080/auth/goopeople
    &response_type=code
    &scope=https://www.googleapis.com/auth/userinfo.profile
```
> Then we redirect to the url above⬆️   
> Then User need to grant to our app  
> <img src="image/login.png" width=200>  
> After grant, the response looks like:
```
http://localhost:8080/auth/goopeople?code=****************
    &scope=profile+https://www.googleapis.com/auth/userinfo.profile
```

# Access token
> AuthService.kt#Line 96: accessGooglePeople()  
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
> Then send request to `https://people.googleapis.com/`. *I had some troubles about unwrap the SDK here, so I can't specify the parameters of request :(*   
> I only get name information.   
> <img src="image/res.png" width=600>  