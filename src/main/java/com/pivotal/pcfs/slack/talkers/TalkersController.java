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
        .getChannelMessageHistory("G04NWJQ90"); // #pcfs-internal

    Map<String, Integer> slackChannelParticipantCharacterCountByUserId =
        slackChannelMessageHistory.stream()
            .filter(slackMessage -> slackMessage != null && slackMessage.getUser() != null)
            .collect(
                Collectors.groupingBy(
                    SlackMessage::getUser,
                    Collectors.reducing(0, (user) -> user.getContent().length(), Integer::sum)
                )
            );

    Map<String, Integer> slackChannelParticipantCharacterCountByUserName = 
        slackChannelParticipantCharacterCountByUserId
        .entrySet().stream()
        .collect(Collectors.toMap(
            e -> slackService.getUserRealName(e.getKey()),
            e -> e.getValue()
        ));

    model.addAttribute("slackChannelName", "pcfs-internal");
    model.addAttribute("slackChannelParticipantCharacterCount",
        slackChannelParticipantCharacterCountByUserName);

    return "visualize";
  }

}
