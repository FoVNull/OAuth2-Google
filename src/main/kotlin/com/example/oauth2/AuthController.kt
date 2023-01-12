package com.example.oauth2

import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class AuthController {
    
    @Autowired
    @Qualifier("AuthService")
    lateinit var authService: IAuthService
    
    @GetMapping("/")
    fun index(): String{
        return "index"
    }
    
    @RequestMapping("/auth/ping")
    @ResponseBody
    fun ping(@RequestParam(value = "msg", defaultValue = "default") msg: String): String{
        return authService.ping(msg)
    }
    
    @RequestMapping("/auth/goocloud")
    fun authGooCloud(response: HttpServletResponse){
        response.sendRedirect(authService.authGooglePeople())
    }

    @RequestMapping("/auth/goopeople")
    fun authGooPeople(@RequestParam(required = false) code: String, @RequestParam(required = false) scope: String,
                      model: Model): String{
        model.addAttribute("profile_info", authService.accessGooglePeople(code))
        return "index"
    }
}