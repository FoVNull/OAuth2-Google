package com.example.oauth2

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.people.v1.PeopleService
import com.google.api.services.people.v1.PeopleServiceScopes
import com.google.api.services.people.v1.model.Person
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.*
import java.security.GeneralSecurityException
import java.util.*


@Service("AuthService")
class AuthService: IAuthService{
    private val APPLICATION_NAME = "Google People API Java Quickstart"
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private val TOKENS_DIRECTORY_PATH = "src/main/resources/static/tokens"

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES: List<String> = Arrays.asList(PeopleServiceScopes.USERINFO_PROFILE)
    
    override fun ping(msg: String): String {
        return "Got '$msg'. Greeting from server"
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    private fun getAccessCredential(HTTP_TRANSPORT: NetHttpTransport): Credential {
        // Load client secrets.
        val inStream: InputStream = ClassPathResource("static/credentials.json").inputStream
        val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inStream))

        // Build flow and trigger user authorization request.
        val flow: GoogleAuthorizationCodeFlow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build()
        val receiver = LocalServerReceiver.Builder().setPort(8081).build()

        val redirectUri: String = receiver.redirectUri
        val authorizationUrl: AuthorizationCodeRequestUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri)
        val credentialURL = authorizationUrl.build()

        // launch browser, get authorization code, login with google account
        val runtime = Runtime.getRuntime()
        runtime.exec("open $credentialURL")
        val authCode: String = receiver.waitForCode()

        val tokenRequest: GoogleAuthorizationCodeTokenRequest = flow.newTokenRequest(authCode).setRedirectUri(redirectUri)
        /* 
        tokenRequest.execute() get response:
        {
         "access_token": "...",
	     "expires_in": 3599,
	     "refresh_token": "...",
	     "scope": "https://www.googleapis.com/auth/contacts.readonly",
	     "token_type": "Bearer"
	     }
         */
        // request code at com.google.api.client.auth.oauth2.TokenRequest.executeUnparsed(TokenRequest.java:304) 
        val tokenResponse: TokenResponse = tokenRequest.execute()
        return flow.createAndStoreCredential(tokenResponse, "author")
    }
    
    private fun getPeopleInfo(credential: Credential, HTTP_TRANSPORT: NetHttpTransport): String{
        val service: PeopleService = PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                                        .setApplicationName(APPLICATION_NAME)
                                        .build()

        val response: Person = service.people().get("people/me")
                .setPersonFields("names,emailAddresses")
                .execute()
        return response.names.toString()
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    override fun authGooglePeople(): String {
        // Build a new authorized API client service.
        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val credential: Credential = getAccessCredential(httpTransport)
        

        return getPeopleInfo(credential, httpTransport)
    }

}