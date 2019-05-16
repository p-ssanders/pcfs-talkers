package com.pivotal.pcfs.slack.talkers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PcfsTalkersApplication {

	public static void main(String[] args) {
		SpringApplication.run(PcfsTalkersApplication.class, args);
	}

	@Configuration
	public static class PcfsTalkersApplicationMvcConfiguration implements WebMvcConfigurer {

		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addRedirectViewController("/", "/visualize");
		}
	}

}
