package com.openlegacy;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

  @RequestMapping("/")
  public String index(){
    return "Welcome to the OpenLegacy TAP demo";
  }
}
