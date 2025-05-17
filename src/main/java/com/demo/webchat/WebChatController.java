package com.demo.webchat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebChatController {

    @GetMapping("/")
    public String index() {
        return "chat";
    }
}
