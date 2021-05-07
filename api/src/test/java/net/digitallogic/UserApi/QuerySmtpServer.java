package net.digitallogic.UserApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.digitallogic.UserApi.config.Profiles;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@ActiveProfiles(Profiles.NON_ASYNC)
public class QuerySmtpServer {

    private final RestTemplate client = new RestTemplate();
    private final String smptRestUrl = "http://localhost:8025/api/v2/";

    public JsonNode queryClient(String value) {

        ResponseEntity<String> response = client.exchange(
                UriComponentsBuilder.fromHttpUrl(smptRestUrl + "search")
                        .queryParam("kind", "containing")
                        .queryParam("query", value)
                        .toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(getHeaders()),
                String.class
        );


        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        return headers;
    }
}
