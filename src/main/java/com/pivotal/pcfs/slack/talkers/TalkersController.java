package com.pivotal.pcfs.slack.talkers;

import java.util.Collection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TalkersController {

  private final SlackService slackService;

  public TalkersController(SlackService slackService) {
    this.slackService = slackService;
  }

  @RequestMapping("/visualize")
  public String visualize(
      @RequestParam(value = "channelName", defaultValue = "pcfs-internal") String channelName,
      Model model
  ) {
    Collection<SlackMessage> slackChannelMessageHistory = slackService
        .getChannelMessageHistory(channelName);

    model.addAttribute("slackChannelName", channelName);
    model.addAttribute("slackChannelMessageHistory", slackChannelMessageHistory);

    return "visualize";
  }

}
