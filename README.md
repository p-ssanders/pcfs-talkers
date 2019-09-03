#   Slack Talkers

Visualize the top contributors to a Slack channel in a pie chart by total characters in their
messages over the last month.

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
    kubectl create secret docker-registry regcred --docker-server=<your-registry-server> --docker-username=<your-name> --docker-password=<your-pword> --docker-email=<your-email>
    kubectl create secret generic slack-api-token --from-literal=SLACK_API_TOKEN=<YOUR SLACK API TOKEN>
    sed "s/dockertag/$(cat Dockertag)/g" k8s-manifest.yml | kubectl apply -f -
    kubectl apply -f k8s-manifest.yml
    ```

### Execute

```http request
GET /?channel-id=<YOUR CHANNEL ID>
```

### Concourse CI

  * Setup Credhub secrets
    ```
    credhub set -n /concourse/main/slack-talkers/git-deploy-key -t ssh --private "$(cat deploy-keys/id_rsa)" --public "$(cat deploy-keys/id_rsa.pub)" && \
    credhub set -n /concourse/main/slack-talkers/dockerhub-username -t value -v <YOUR DOCKERHUB USERNAME> && \
    credhub set -n /concourse/main/slack-talkers/dockerhub-password -t value -v <YOUR DOCKERHUB PASSWORD> && \
    credhub set -n /concourse/main/slack-talkers/pivnet-api-token -t value -v <YOUR PIVNET API TOKEN> && \
    credhub set -n /concourse/main/slack-talkers/pks-api-username -t value -v <YOUR PKS API USERNAME> && \
    credhub set -n /concourse/main/slack-talkers/pks-api-password -t value <YOUR PKS API PASSWORD> && \
    credhub set -n /concourse/main/slack-talkers/lets_encrypt_cert -t certificate -r "$(cat ca.pem)" -c "$(cat certificate.pem)" -p "$(cat private_key.pem)"
    ```

  * Create the pipeline
    ```
    fly -t sam-ci set-pipeline -p slack-talkers -c ci/pipeline.yml
    ```

### Tools
*   [Spring Boot](https://spring.io/projects/spring-boot)
*   [Spring Cache](https://spring.io/guides/gs/caching/)
*   [Thymeleaf](https://www.thymeleaf.org/)
*   [Google Charts](https://developers.google.com/chart/)
*   [Slack API](https://api.slack.com/)