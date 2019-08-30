#   Slack Talkers

Spring web app that uses Slack channel message history to display the top contributors
in a pie chart by total characters in their messages over the last month.

### Tools
*   [Spring Boot](https://spring.io/projects/spring-boot)
*   [Spring Cache](https://spring.io/guides/gs/caching/)
*   [Thymeleaf](https://www.thymeleaf.org/)
*   [Google Charts](https://developers.google.com/chart/)
*   [Slack API](https://api.slack.com/)

### Build
```bash
./mvnw clean install
```

### Run

  * Cloud
    ```
    SLACK_API_TOKEN=<YOUR SLACK API TOKEN> ./mvnw spring-boot:run
    ```

  * Kubernetes
    ```
    kubectl create secret generic slack-api-token --from-literal=SLACK_API_TOKEN=<YOUR SLACK API TOKEN>
    kubectl apply -f k8s-manifest.yml
    ```

### Execute

```http request
GET /?channel-id=<YOUR CHANNEL ID>
```

### Concourse CI

  * Setup Credhub secrets
    ```
    credhub set -n /concourse/main/slack-talkers/git-deploy-key -t ssh --private "$(cat deploy-keys/id_rsa)" --public "$(cat deploy-keys/id_rsa.pub)"
    ```

  * Create the pipeline
    ```
    fly -t sam-ci set-pipeline -p slack-talkers -c ci/pipeline.yml
    ```
