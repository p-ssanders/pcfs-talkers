#   PCFS Talkers

Spring web app that uses Slack channel message history to display the top contributors
in a pie chart by total characters in their messages over the last month.

#### Build
```
./mvnw clean install
```

#### Run
```
SLACK_API_TOKEN=<YOUR SLACK API TOKEN> ./mvnw spring-boot:run
```

#### Tooling
*   [Spring Boot](https://spring.io/projects/spring-boot)
*   [Spring Cache](https://spring.io/guides/gs/caching/)
*   [Thymeleaf](https://www.thymeleaf.org/)
*   [Google Charts](https://developers.google.com/chart/)
*   [Slack API](https://api.slack.com/)
