package com.example.oauth2

interface IAuthService {
    fun ping(msg: String): String
    
    fun authGooglePeople(): String
    
    fun accessGooglePeople(authCode: String): String
}