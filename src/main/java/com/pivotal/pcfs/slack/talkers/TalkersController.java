package com.pivotal.pcfs.slack.talkers;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
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

    Map<String, Integer> slackChannelParticipantCharacterCount =
        slackChannelMessageHistory.stream()
        .collect(
            Collectors.groupingBy(
                SlackMessage::getUser,
                Collectors.reducing(0, (user) -> user.getContent().length(), Integer::sum)
            )
        );

    model.addAttribute("slackChannelName", channelName);
    model.addAttribute("slackChannelParticipantCharacterCount", slackChannelParticipantCharacterCount);

    return "visualize";
  }

}
