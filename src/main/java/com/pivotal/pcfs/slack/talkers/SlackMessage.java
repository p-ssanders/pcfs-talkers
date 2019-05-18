package com.pivotal.pcfs.slack.talkers;

import java.util.Objects;
import javax.validation.constraints.NotNull;

public class SlackMessage {

  private final String user;
  private final String content;
  private final String timestamp;

  public SlackMessage(@NotNull String user, @NotNull String content, String timestamp) {
    this.user = user;
    this.content = content;
    this.timestamp = timestamp;
  }

  public String getUser() {
    return user;
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
    return Objects.equals(user, that.user) &&
        Objects.equals(content, that.content) &&
        Objects.equals(timestamp, that.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, content, timestamp);
  }

  @Override
  public String toString() {
    return "SlackMessage{" +
        "user='" + user + '\'' +
        ", content='" + content + '\'' +
        ", timestamp='" + timestamp + '\'' +
        '}';
  }
}