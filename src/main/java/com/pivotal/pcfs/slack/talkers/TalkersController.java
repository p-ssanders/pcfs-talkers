package com.pivotal.pcfs.slack.talkers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TalkersController {

  @RequestMapping("/visualize")
  public String visualize(Model model) {
    model.addAttribute("name", "World");
    return "visualize";
  }

}
