package com.pivotal.pcfs.slack.talkers;

import java.util.Objects;
import javax.validation.constraints.NotNull;

public class SlackMessage {

  private final String user;
  private final String content;

  public SlackMessage(@NotNull String user, @NotNull String content) {
    this.user = user;
    this.content = content;
  }

  public String getUser() {
    return user;
  }

  public String getContent() {
    return content;
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
    return user.equals(that.user) &&
        content.equals(that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, content);
  }

  @Override
  public String toString() {
    return "SlackMessage{" +
        "user='" + user + '\'' +
        ", content='" + content + '\'' +
        '}';
  }
}