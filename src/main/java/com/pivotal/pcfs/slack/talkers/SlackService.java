package com.pivotal.pcfs.slack.talkers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
    ChannelMessageHistory channelMessageHistory =
        restTemplate.getForObject(
            SLACK_API_ROOT + "/groups.history?token={token}&channel={channel}",
            ChannelMessageHistory.class, slackApiToken, channelId
        );

    if(!channelMessageHistory.isOk()) {
      throw new IllegalStateException("Can't get channel history");
    }

    List<SlackMessage> slackMessages = channelMessageHistory
        .getMessages().stream()
        .map(message -> new SlackMessage(message.getUser(), message.getText()))
        .collect(Collectors.toList());

    return slackMessages;
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

    @JsonCreator
    public Message(@JsonProperty("user") String user, @JsonProperty("text") String text) {
      this.user = user;
      this.text = text;
    }

    public String getUser() {
      return user;
    }

    public String getText() {
      return text;
    }

    @Override
    public String toString() {
      return "Message{" +
          "user='" + user + '\'' +
          ", text='" + text + '\'' +
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
      return user.equals(message.user) &&
          text.equals(message.text);
    }

    @Override
    public int hashCode() {
      return Objects.hash(user, text);
    }
  }

}
