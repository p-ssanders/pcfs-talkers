package com.pivotal.pcfs.slack.talkers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

public class SlackService {

  private static final String SLACK_API_ROOT = "https://slack.com/api";

  private final String slackApiToken;
  private final RestTemplate restTemplate;

  public SlackService(String slackApiToken, RestTemplate restTemplate) {
    this.slackApiToken = slackApiToken;
    this.restTemplate = restTemplate;
  }

  public Collection<SlackMessage> getChannelMessageHistory(String channelId) {
    List<SlackMessage> slackMessages = new ArrayList<>();
    String oldestMessageLimitTimestamp = String
        .valueOf(LocalDate.now().minusMonths(1).toEpochSecond(LocalTime.now(), ZoneOffset.UTC));

    ChannelMessageHistory channelMessageHistory;
    String lastMessageTimestamp = "";
    do {
      channelMessageHistory = doGetChannelMessageHistoryPagination(channelId, lastMessageTimestamp);

      slackMessages.addAll(channelMessageHistory
          .getMessages().stream()
          .map(message -> new SlackMessage(message.getUser(), message.getText(),
              message.getTimestamp()))
          .collect(Collectors.toList()));

      lastMessageTimestamp = slackMessages.get(slackMessages.size() - 1).getTimestamp();
      if (lastMessageTimestamp.compareTo(oldestMessageLimitTimestamp) < 0) {
        break;
      }

    } while (channelMessageHistory.isHasMore());

    return slackMessages;
  }

  private ChannelMessageHistory doGetChannelMessageHistoryPagination(String channelId,
      String lastMessageTimestamp) {

    if (lastMessageTimestamp.equals("")) {
      return doGetChannelMessageHistory(channelId);
    }

    ChannelMessageHistory channelMessageHistory = restTemplate.getForObject(
        SLACK_API_ROOT
            + "/groups.history?token={token}&channel={channel}&count=1000&latest={timestamp}",
        ChannelMessageHistory.class, slackApiToken, channelId, lastMessageTimestamp
    );

    if (!channelMessageHistory.isOk()) {
      throw new IllegalStateException("Can't get channel history");
    }

    return channelMessageHistory;
  }

  private ChannelMessageHistory doGetChannelMessageHistory(String channelId) {
    ChannelMessageHistory channelMessageHistory =
        restTemplate.getForObject(
            SLACK_API_ROOT + "/groups.history?token={token}&channel={channel}&count=1000",
            ChannelMessageHistory.class, slackApiToken, channelId
        );

    if (!channelMessageHistory.isOk()) {
      throw new IllegalStateException("Can't get channel history");
    }

    return channelMessageHistory;
  }

  @Cacheable("slackUserRealNames")
  public String getUserRealName(String userId) {
    SlackUserDetails slackUserDetails = restTemplate
        .getForObject(SLACK_API_ROOT + "/users.info?token={token}&user={userId}",
            SlackUserDetails.class, slackApiToken, userId);

    if(!slackUserDetails.isOk())
      throw new IllegalStateException("Can't get user real name");

    if(slackUserDetails.getSlackUser() == null)
      throw new IllegalArgumentException(String.format("No user with userId %s", userId));

    if(slackUserDetails.getSlackUser().getRealName() == null)
      throw new IllegalArgumentException(String.format("No user real name with userId %s", userId));

    return slackUserDetails.getSlackUser().getRealName();
  }

  private static class ChannelMessageHistory {

    private boolean ok;
    private Collection<Message> messages;
    private boolean hasMore;

    @JsonCreator
    public ChannelMessageHistory(
        @JsonProperty("ok") boolean ok,
        @JsonProperty("messages") Collection<Message> messages,
        @JsonProperty("has_more") boolean hasMore
    ) {
      this.ok = ok;
      this.messages = messages;
      this.hasMore = hasMore;
    }

    public boolean isOk() {
      return ok;
    }

    public Collection<Message> getMessages() {
      return messages;
    }

    public boolean isHasMore() {
      return hasMore;
    }

    @Override
    public String toString() {
      return "ChannelMessageHistory{" +
          "ok=" + ok +
          ", messages=" + messages +
          ", hasMore=" + hasMore +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof ChannelMessageHistory)) {
        return false;
      }
      ChannelMessageHistory that = (ChannelMessageHistory) o;
      return ok == that.ok &&
          hasMore == that.hasMore &&
          Objects.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
      return Objects.hash(ok, messages, hasMore);
    }
  }

  private static class Message {

    private String user;
    private String text;
    private String timestamp;

    @JsonCreator
    public Message(@JsonProperty("user") String user, @JsonProperty("text") String text,
        @JsonProperty("ts") String timestamp) {
      this.user = user;
      this.text = text;
      this.timestamp = timestamp;
    }

    public String getUser() {
      return user;
    }

    public String getText() {
      return text;
    }

    public String getTimestamp() {
      return timestamp;
    }

    @Override
    public String toString() {
      return "Message{" +
          "user='" + user + '\'' +
          ", text='" + text + '\'' +
          ", timestamp='" + timestamp + '\'' +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Message)) {
        return false;
      }
      Message message = (Message) o;
      return Objects.equals(user, message.user) &&
          Objects.equals(text, message.text) &&
          Objects.equals(timestamp, message.timestamp);
    }

    @Override
    public int hashCode() {
      return Objects.hash(user, text, timestamp);
    }
  }

  private static class SlackUserDetails {

    private boolean ok;
    private SlackUser slackUser;

    @JsonCreator
    public SlackUserDetails(
        @JsonProperty("ok") boolean ok,
        @JsonProperty("user") SlackUser slackUser
    ) {
      this.ok = ok;
      this.slackUser = slackUser;
    }

    public boolean isOk() {
      return ok;
    }

    public SlackUser getSlackUser() {
      return slackUser;
    }

    @Override
    public String toString() {
      return "SlackUserDetails{" +
          "ok=" + ok +
          ", slackUser=" + slackUser +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof SlackUserDetails)) {
        return false;
      }
      SlackUserDetails that = (SlackUserDetails) o;
      return ok == that.ok &&
          Objects.equals(slackUser, that.slackUser);
    }

    @Override
    public int hashCode() {
      return Objects.hash(ok, slackUser);
    }
  }

  private static class SlackUser {
    private String realName;

    @JsonCreator
    public SlackUser(@JsonProperty("real_name") String realName) {
      this.realName = realName;
    }

    public String getRealName() {
      return realName;
    }

    @Override
    public String toString() {
      return "SlackUser{" +
          "realName='" + realName + '\'' +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof SlackUser)) {
        return false;
      }
      SlackUser slackUser = (SlackUser) o;
      return Objects.equals(realName, slackUser.realName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(realName);
    }
  }

}
