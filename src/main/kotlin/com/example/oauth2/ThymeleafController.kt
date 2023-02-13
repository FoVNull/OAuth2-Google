package com.example.oauth2

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class ThymeleafController {
    @RequestMapping("/thtest")
    fun thymeleafTest(model: Model): String {
        val obj = Obj(mapOf<String, String>("at1k" to "at1v"), mapOf<String, String>("at2k" to "false"))
        model.addAttribute("obj", obj)
        
        return "index"
    }
}

class Obj(v1: Map<String, String>, v2: Map<String, String>) {
    var attr1: Map<String, String> = v1
    var attr2: Map<String, String> = v2
}