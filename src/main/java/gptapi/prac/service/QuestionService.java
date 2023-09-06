package gptapi.prac.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gptapi.prac.config.Config;
import gptapi.prac.dto.gpt.ChatGptMessage;
import gptapi.prac.dto.gpt.GptRequest;
import gptapi.prac.dto.gpt.GptResponse;
import gptapi.prac.dto.notion.NotionLinkRequest;
import gptapi.prac.dto.notion.NotionResponse;
import gptapi.prac.entity.Question;
import gptapi.prac.entity.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gptapi.prac.config.Config.*;
import static gptapi.prac.util.StringUtil.slice;

@Service
@RequiredArgsConstructor
@Log4j2
public class QuestionService {

    private final QuestionRepository repository;
    private final RestTemplate restTemplate;

    @Value("${openai.apiKey}")
    private String apiKey;

    @Value("${notion.api.key}")
    private String n_apiKey;

    @Value("${notion.api.version}")
    private String version;

    public Question createQuestion(NotionLinkRequest request) throws JsonProcessingException {

        String text = getNotionPagesTexts((request.getNotionLink()));
        String question = generateQuestions(text);

        //저장
        Question answer = savedQuestion(question);

        return answer;
    }

    private String getNotionPagesTexts(String link) throws JsonProcessingException {
        // ...노션에서 plain_text들을 가져와서 합치기
        String pageId = slice(link, -32);

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, BEARER + n_apiKey);
        headers.add("Notion-Version", version);
        headers.add("Content-Type", MEDIA_TYPE);
        HttpEntity httpEntity = new HttpEntity(headers);

        String NOTION_URL = "https://api.notion.com/v1/blocks/" + pageId + "/children";

        ResponseEntity<String> object = restTemplate.exchange(NOTION_URL, HttpMethod.GET, httpEntity, String.class);

        String str = object.getBody();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readValue(str, JsonNode.class);
        JsonNode results = jsonNode.get("results");
        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {

            String type = results.get(i).get("type").asText();

            JsonNode richText = results.get(i).get(type).get("rich_text");

            if (richText == null || richText.isNull()) {
                continue;
            }

            List<NotionResponse> response = Arrays.asList(objectMapper.readValue(richText.toString(), NotionResponse[].class));

            response.stream().forEach(r -> {
                String plainText = r.getPlainText();
                textBuilder.append(plainText).append(" ");
            });
        }

        String text = textBuilder.toString();

        log.info(text);
        return text;
    }

    private String generateQuestions(String text) {
        // ... gpt를통해 문제를 생성
        List<ChatGptMessage> messages = new ArrayList<>();
        //system prompt 생성
        messages.add(ChatGptMessage.builder()
                .role("system")
                .content(SYSTEM_CONTENT)
                .build());
        //example prompt
        messages.add(ChatGptMessage.builder()
                .role("user")
                .content(USER_CONTENT)
                .build());
        //assistant prompt
        messages.add(ChatGptMessage.builder()
                .role("assistant")
                .content(ASSISTANT_CONTENT)
                .build());
        //user prompt
        messages.add(ChatGptMessage.builder()
                .role("user")
                .content("[지문]\n" + text)
                .build());

        GptRequest request = new GptRequest(CHAT_MODEL, MAX_TOKEN, TEMPERATURE, STREAM, messages);

        //header 설정 및 GPT API 호출
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, BEARER + apiKey);
        headers.add("Content-Type", MEDIA_TYPE);

        HttpEntity<GptRequest> requestHttpEntity = new HttpEntity<>(request, headers);

        GptResponse response = restTemplate.postForObject(Config.CHAT_URL, requestHttpEntity, GptResponse.class);

        String content = response.getChoices().get(0).getMessage().getContent();

        return content;
    }

    // 문제 저장
    public Question savedQuestion(String question) {
        Question answer = new Question(question);
        return repository.save(answer);
    }
}
