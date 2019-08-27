package com.pivotal.pcfs.slack.talkers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
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

  @Cacheable("slackUserRealNames")
  public String getUserRealName(String userId) {
    SlackUserDetails slackUserDetails = restTemplate
        .getForObject(SLACK_API_ROOT + "/users.info?token={token}&user={userId}",
            SlackUserDetails.class, slackApiToken, userId);

    if (!slackUserDetails.isOk())
      throw new IllegalStateException("Can't get user real name");

    if (slackUserDetails.getSlackUser() == null)
      throw new IllegalArgumentException(String.format("No user with userId %s", userId));

    if (slackUserDetails.getSlackUser().getRealName() == null)
      throw new IllegalArgumentException(String.format("No user real name with userId %s", userId));

    return slackUserDetails.getSlackUser().getRealName();
  }

  public Collection<SlackMessage> getChannelMessages(String channelId) {
    String oneMonthAgo = oneMonthAgo();

    ChannelMessages channelMessages = getLatestChannelMessages(channelId);

    ChannelMessages _channelMessages = channelMessages;
    while (_channelMessages.isHasMore()) {

      String oldestMessageTimestamp = channelMessages.getOldestMessageTimestamp();

      // if oldestMessageTimestamp is before oneMonthAgo bc thats how Slack does timestamps
      if (oldestMessageTimestamp.compareTo(oneMonthAgo) < 0)
        break;

      _channelMessages = getChannelMessagesStartingFrom(channelId, oldestMessageTimestamp);

      channelMessages.addAll(_channelMessages);
    }

    return channelMessages.asSlackMessages();
  }

  private static String oneMonthAgo() {
    return String.valueOf(LocalDate.now().minusMonths(1).toEpochSecond(LocalTime.now(), ZoneOffset.UTC));
  }

  private ChannelMessages getLatestChannelMessages(String channelId) {
    ChannelMessages channelMessages =
        restTemplate.getForObject(
            SLACK_API_ROOT + "/groups.history?token={token}&channel={channel}&count=1000",
            ChannelMessages.class, slackApiToken, channelId
        );

    if (!channelMessages.isOk()) {
      throw new IllegalStateException("Can't get channel history");
    }

    return channelMessages;
  }

  private ChannelMessages getChannelMessagesStartingFrom(String channelId, String startFromTimestamp) {
    ChannelMessages channelMessages = restTemplate.getForObject(
        SLACK_API_ROOT
            + "/groups.history?token={token}&channel={channel}&count=1000&latest={timestamp}",
        ChannelMessages.class, slackApiToken, channelId, startFromTimestamp
    );

    if (!channelMessages.isOk()) {
      throw new IllegalStateException("Can't get channel history");
    }

    return channelMessages;
  }

  private static class ChannelMessages {

    private boolean ok;
    private List<Message> messages;
    private boolean hasMore;

    @JsonCreator
    public ChannelMessages(
        @JsonProperty("ok") boolean ok,
        @JsonProperty("messages") List<Message> messages,
        @JsonProperty("has_more") boolean hasMore
    ) {
      this.ok = ok;
      this.messages = messages;
      this.hasMore = hasMore;
    }

    public boolean isOk() {
      return ok;
    }

    public List<Message> getMessages() {
      return messages;
    }

    public boolean isHasMore() {
      return hasMore;
    }

    public void addAll(ChannelMessages channelMessages) {
      this.messages.addAll(channelMessages.getMessages());
    }

    public String getOldestMessageTimestamp() {
      return this.getMessages().get(this.getMessages().size() - 1).getTimestamp();
    }

    @Override
    public String toString() {
      return "ChannelMessages{" +
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
      if (!(o instanceof ChannelMessages)) {
        return false;
      }
      ChannelMessages that = (ChannelMessages) o;
      return ok == that.ok &&
          hasMore == that.hasMore &&
          Objects.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
      return Objects.hash(ok, messages, hasMore);
    }

    public Collection<SlackMessage> asSlackMessages() {
      return this
          .getMessages().stream()
          .map(message -> new SlackMessage(message.getUser(), message.getText(),
              message.getTimestamp()))
          .collect(Collectors.toList());
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
