package com.pivotal.slack.talkers;

import java.util.Objects;
import javax.validation.constraints.NotNull;

public class SlackMessage {

  private final String userId;
  private final String content;
  private final String timestamp;

  public SlackMessage(@NotNull String userId, @NotNull String content, String timestamp) {
    this.userId = userId;
    this.content = content;
    this.timestamp = timestamp;
  }

  public String getUserId() {
    return userId;
  }

  public String getContent() {
    return content;
  }

  public String getTimestamp() {
    return timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SlackMessage)) {
      return false;
    }
    SlackMessage that = (SlackMessage) o;
    return Objects.equals(userId, that.userId) &&
        Objects.equals(content, that.content) &&
        Objects.equals(timestamp, that.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, content, timestamp);
  }

  @Override
  public String toString() {
    return "SlackMessage{" +
        "userId='" + userId + '\'' +
        ", content='" + content + '\'' +
        ", timestamp='" + timestamp + '\'' +
        '}';
  }
}