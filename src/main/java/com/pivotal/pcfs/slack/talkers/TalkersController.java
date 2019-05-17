package com.pivotal.pcfs.slack.talkers;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TalkersController {

  private final SlackService slackService;

  public TalkersController(SlackService slackService) {
    this.slackService = slackService;
  }

  @RequestMapping("/visualize")
  public String visualize(Model model) {
    Collection<SlackMessage> slackChannelMessageHistory = slackService
        .getChannelMessageHistory("G04NWJQ90");

    Map<String, Integer> slackChannelParticipantCharacterCount =
        slackChannelMessageHistory.stream()
            .collect(
                Collectors.groupingBy(
                    SlackMessage::getUser,
                    Collectors.reducing(0, (user) -> user.getContent().length(), Integer::sum)
                )
            );

    model.addAttribute("slackChannelName", "pcfs-internal");
    model.addAttribute("slackChannelParticipantCharacterCount",
        slackChannelParticipantCharacterCount);

    return "visualize";
  }

}
