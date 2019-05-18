#   PCFS Talkers

Visualize the primary talkers in `#pcfs-internal`

#### Build
```
mvnw clean install
```

#### Run
```
SLACK_API_TOKEN=<YOUR SLACK API TOKEN> mvnw spring-boot:run
```

#### Details

Spring web app that gets Slack channel chat history on-demand, and displays the primary participants
in a pie chart by total characters in their messages over the last month.

