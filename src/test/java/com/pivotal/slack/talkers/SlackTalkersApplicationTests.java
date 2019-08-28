package com.pivotal.slack.talkers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureWebClient
public class SlackTalkersApplicationTests {

  @Test
  public void contextLoads() {
  }

}
