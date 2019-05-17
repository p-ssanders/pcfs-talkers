package com.pivotal.pcfs.slack.talkers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@RunWith(SpringRunner.class)
@WebMvcTest
public class TalkersControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private SlackService mockSlackService;

  @Before
  public void setUp() {
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setPrefix("templates/");
    templateResolver.setSuffix(".html");
    templateResolver.setCharacterEncoding("UTF-8");

    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);

    ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
    thymeleafViewResolver.setTemplateEngine(templateEngine);
    thymeleafViewResolver.setCharacterEncoding("UTF-8");

    this.mockMvc = MockMvcBuilders.standaloneSetup(new TalkersController(mockSlackService))
        .setViewResolvers(thymeleafViewResolver).build();
  }

  @Test
  public void visualize() throws Exception {
    Collection<SlackMessage> slackChannelMessages =
        List.of(
             new SlackMessage("some-user-1", "some-message")
            ,new SlackMessage("some-user-1", "some-message")
            ,new SlackMessage("some-user-1", "some-message")
            ,new SlackMessage("some-user-2", "some-message")
            ,new SlackMessage("some-user-2", "some-message")
            ,new SlackMessage("some-user-3", "some-message")
        );
    when(mockSlackService.getChannelMessageHistory("some-channel-name"))
        .thenReturn(slackChannelMessages);

    this.mockMvc.perform(
        MockMvcRequestBuilders.get("/visualize").param("channelName", "some-channel-name")
    )
    .andExpect(
        status().isOk()
    )
    .andExpect(
        content().contentType("text/html;charset=UTF-8")
    )
    .andExpect(
        model().attribute("slackChannelName", "some-channel-name")
    )
    .andExpect(
        model().attribute("slackChannelParticipantCharacterCount",
            Map.of(
                "some-user-1", "some-message".length() * 3,
                "some-user-2", "some-message".length() * 2,
                "some-user-3", "some-message".length() * 1
            )
        )
    );
  }

}
