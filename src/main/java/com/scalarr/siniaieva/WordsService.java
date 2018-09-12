package com.scalarr.siniaieva;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class WordsService {

    private final static String X_MASHAPE_KEY_HEADER = "X-Mashape-Key";
    private final static String X_MASHAPE_KEY_VALUE = "7L7XcGj5LLmshQBh3rWZy2oxAyZhp1FkMHPjsnP1or1VCLz6lV";
    private final static String X_MASHAPE_HOST_HEADER = "X-Mashape-Host";
    private final static String X_MASHAPE_HOST_VALUE = "wordsapiv1.p.mashape.com";

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private HttpHeaders headers;

    public WordsService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.headers = new HttpHeaders();
        headers.add(X_MASHAPE_HOST_HEADER, X_MASHAPE_HOST_VALUE);
        headers.add(X_MASHAPE_KEY_HEADER, X_MASHAPE_KEY_VALUE);
    }

    public List<String> listOfWordsByLength(int length) {
        String uri = UriBuilder.build(UriBuilder.LENGTH, length);
        JsonNode jsonNode = executeGet(uri, length);
        return readList(jsonNode, String.class);
    }

    public List<String> listOfWordsByPattern(String pattern) {
        String uri = UriBuilder.build(UriBuilder.PATTERN, pattern);
        JsonNode jsonNode = executeGet(uri);
        return readList(jsonNode, String.class);
    }

    public Double getFrequency(String word) {
        String uri = UriBuilder.build(UriBuilder.FREQUENCY, word);
        JsonNode jsonNode = executeGet(uri);
        JsonNode freq = jsonNode.get("frequency").get("perMillion");
        return readObject(freq, Double.class);
    }

    @SuppressWarnings("unchecked")

    private <T> List<T> readList(JsonNode jsonNode, Class<T> type) {
        CollectionType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, type);
        return (List<T>) objectMapper.convertValue(jsonNode, listType);
    }

    private <T> T readObject(JsonNode jsonNode, Class<T> type) {
        return (T) objectMapper.convertValue(jsonNode, type);
    }

    private JsonNode executeGet(String uri, Object arg) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class, arg);
        return response.getBody();
    }

    private JsonNode executeGet(String uri) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody();
    }

    @AllArgsConstructor
    @Getter
    enum UriBuilder {
        PATTERN("?letterPattern=%s"),
        LENGTH("?letterPattern=^[a-zA-Z]{%d}$"),
        FREQUENCY("%s/frequency");

        private final static String URL = "https://wordsapiv1.p.mashape.com/words/";
        private final String path;

        public static String build(UriBuilder type, Object arg) {
            return String.format(URL + type.getPath(), arg);
        }


    }

}
