package com.example.oauth2

interface IAuthService {
    fun ping(msg: String): String
    
    fun authGooglePeople(ifOIDC:Boolean): String
    
    fun accessGooglePeople(authCode: String, ifOIDC: Boolean): String
}