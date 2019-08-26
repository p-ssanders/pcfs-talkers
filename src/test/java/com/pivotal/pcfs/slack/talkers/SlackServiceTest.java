package com.pivotal.pcfs.slack.talkers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
public class SlackServiceTest {

  private RestTemplate restTemplate = new RestTemplate();
  private MockRestServiceServer mockRestServiceServer = MockRestServiceServer
      .createServer(restTemplate);
  private SlackService slackService;

  @Before
  public void setUp() {
    mockRestServiceServer.reset();
    slackService = new SlackService("some-slack-api-token", restTemplate);
  }

  @Test
  public void getChannelMessageHistory() {
    mockRestServiceServer
        .expect(requestTo(
            "https://slack.com/api/groups.history?token=some-slack-api-token&channel=some-channel-id&count=1000"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(
            "{\"ok\": true,\n"
                + "\"messages\": [{\n"
                + "            \"client_msg_id\": \"00000000-0000-0000-0000-0000000000000\",\n"
                + "            \"type\": \"message\",\n"
                + "            \"text\": \"(feel free to DM me)\",\n"
                + "            \"user\": \"ABCDEFGHI\",\n"
                + "            \"ts\": \"1557962476.069900\",\n"
                + "            \"thread_ts\": \"1557950340.004100\",\n"
                + "            \"parent_user_id\": \"XXXXXXXXX\"\n"
                + "        },"
                + "        {\n"
                + "            \"client_msg_id\": \"00000000-0000-0000-0000-0000000000000\",\n"
                + "            \"type\": \"message\",\n"
                + "            \"text\": \"can you help me understand why?\",\n"
                + "            \"user\": \"123456789\",\n"
                + "            \"ts\": \"1557962417.069700\",\n"
                + "            \"thread_ts\": \"1557950340.004100\",\n"
                + "            \"parent_user_id\": \"XXXXXXXXX\"\n"
                + "        }],\n"
                + "\"has_more\": false\n"
                + "}",
            MediaType.APPLICATION_JSON_UTF8
        ));

    Collection<SlackMessage> channelMessageHistory = slackService
        .getChannelMessages("some-channel-id");

    mockRestServiceServer.verify();

    assertThat(channelMessageHistory)
        .contains(new SlackMessage("ABCDEFGHI", "(feel free to DM me)", "1557962476.069900"));
    assertThat(channelMessageHistory)
        .contains(new SlackMessage("123456789", "can you help me understand why?", "1557962417.069700"));
  }

  @Test
  public void getChannelMessageHistory_notOk() {
    mockRestServiceServer
        .expect(requestTo(
            "https://slack.com/api/groups.history?token=some-slack-api-token&channel=some-channel-id&count=1000"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(
            "{\"ok\": false,\n"
                + "\"messages\": [],\n"
                + "\"has_more\": false\n"
                + "}",
            MediaType.APPLICATION_JSON_UTF8
        ));
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> slackService
        .getChannelMessages("some-channel-id"));

    mockRestServiceServer.verify();
  }

  @Test
  public void getUserRealName() {
    mockRestServiceServer
        .expect(requestTo(
            "https://slack.com/api/users.info?token=some-slack-api-token&user=some-user-id"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(
            "{\n"
                + "    \"ok\": true,\n"
                + "    \"user\": {\n"
                + "        \"real_name\": \"Sammy Sand\"\n"
                + "    }\n"
                + "}",
            MediaType.APPLICATION_JSON_UTF8
        ));
    String userRealName = slackService.getUserRealName("some-user-id");

    mockRestServiceServer.verify();

    assertThat(userRealName).isEqualTo("Sammy Sand");
  }

  @Test
  public void getUserRealNameThrowsExceptionNotOk() {
    mockRestServiceServer
        .expect(requestTo("https://slack.com/api/users.info?token=some-slack-api-token&user=some-user-id"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{\n"
                + "    \"ok\": false\n"
                + "}",
            MediaType.APPLICATION_JSON_UTF8
        ));

    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
      slackService.getUserRealName("some-user-id");
    });

    mockRestServiceServer.verify();
  }

  @Test
  public void getUserRealNameThrowsExceptionWhenNotFound() {
    mockRestServiceServer
        .expect(requestTo("https://slack.com/api/users.info?token=some-slack-api-token&user=some-user-id"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{\n"
                + "    \"ok\": true\n"
                + "}",
            MediaType.APPLICATION_JSON_UTF8
        ));

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> slackService.getUserRealName("some-user-id"));

    mockRestServiceServer.verify();
  }
}