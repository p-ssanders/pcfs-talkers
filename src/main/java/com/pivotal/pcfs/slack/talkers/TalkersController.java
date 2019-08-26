package com.pivotal.pcfs.slack.talkers;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TalkersController {

  public static final String PCFS_INTERNAL = "G04NWJQ90";
  private final SlackService slackService;

  public TalkersController(SlackService slackService) {
    this.slackService = slackService;
  }

  @RequestMapping("/visualize")
  public String visualize(Model model) {
    Collection<SlackMessage> slackChannelMessageHistory = slackService.getChannelMessages(PCFS_INTERNAL);

    Map<String, Integer> realNameToTotalCharacterCount = mapRealNameToTotalCharacterCount(slackChannelMessageHistory);

    model.addAttribute("slackChannelName", "pcfs-internal");
    model.addAttribute("slackChannelParticipantCharacterCount", realNameToTotalCharacterCount);

    return "visualize";
  }

  private Map<String, Integer> mapRealNameToTotalCharacterCount(Collection<SlackMessage> slackChannelMessageHistory) {
    Map<String, Integer> realNameToTotalCharacterCountMap =
        slackChannelMessageHistory.stream()
            .filter(notNull())
            .filter(userIdNotNull())
            .collect(groupByRealNameAndTotalCharacterCount());

    return realNameToTotalCharacterCountMap;
  }

  private Predicate<SlackMessage> notNull() {
    return slackMessage -> slackMessage != null;
  }

  private Predicate<SlackMessage> userIdNotNull() {
    return slackMessage -> slackMessage.getUserId() != null;
  }

  private Collector<SlackMessage, ?, Map<String, Integer>> groupByRealNameAndTotalCharacterCount() {
    return groupingBy(
        this::getSlackUserRealName,
        reducing(0, getSlackMessageLength(), Integer::sum)
    );
  }

  private String getSlackUserRealName(SlackMessage slackMessage) {
    String userRealName;
    try {
      userRealName = slackService.getUserRealName(slackMessage.getUserId());
    }
    catch (Exception e) {
      userRealName = "";
    }
    return userRealName;
  }

  private Function<SlackMessage, Integer> getSlackMessageLength() {
    return (slackMessage) -> slackMessage.getContent().length();
  }
}
