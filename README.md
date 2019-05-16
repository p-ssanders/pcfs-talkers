#   PCFS Talkers

Visualize the primary talkers in `#pcfs-internal`

#### Build
```
mvnw clean install
```

#### Run
```
mvnw spring-boot:run
```

#### Details

Spring web app that gets Slack channel chat history on-demand, and displays the primary participants
in a pie chart by message count.

Potential optimizations:
- Cache the data, expire daily
