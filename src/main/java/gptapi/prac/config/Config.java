package gptapi.prac.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class Config {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    public static final String CHAT_MODEL = "gpt-3.5-turbo";
    public static final Integer MAX_TOKEN = 500;
    public static final Boolean STREAM = false;
    public static final Double TEMPERATURE = 0.5;
    public static final String MEDIA_TYPE = "application/json";

    public static final String SYSTEM_CONTENT = "You're a question taker for the world's leading question taker.\n" +
            "Generate 5 multiple-choice questions, views, answers, and background information for a given fingerprint.\n" +
            "Don't allow multiple answer.\n" +
            "The criteria you'll use to create multiple-choice questions include\n" +
            "1. The question should be clear and easy to understand.\n" +
            "2. there should be only one correct answer.\n" +
            "3. the question sentence should be concise and clear.\n" +
            "4. Choose options that allow students to understand and apply key concepts.\n" +
            "Consider these criteria when creating your multiple choice questions. \n" +
            "For the background information, tell us the underlying paragraph that created the question.\n" +
            "If the given fingerprint is in Korean, both the question and the example must be in Korean.";

    public static final String USER_CONTENT = "QueryDSL은 SQL, JPQL 등을 코드로 작성할 수 있는 빌더 오픈소스 프레임워크다. QueryDSL은 동적 쿼리 문제 해결, 쉬운 SQL 스타일 문법, 문법 오류를 컴파일 시점에 알려주는 특징이 있습니다.";

    public static final String ASSISTANT_CONTENT = "[1] \n\n" +
            "QueryDSL의 특징으로 잘못된 것은? \n" +
            "[1]QueryDSL은 빌더 오픈소스 프레임워크다.\n" +
            "[2] QueryDSL은 동적 쿼리 문제를 해결한다.\n" +
            "[3] QueryDSL은 어려운 SQL 스타일 문법이다.\n" +
            "[4] QueryDSL은 문법 오류를 컴파일 시점에 알려준다.\n" +
            "[5] QueryDSL은 SQL, JPQL 등을 코드로 작성할 수 있다.\n\n" +
            "[정답] 3번 \n\n" + "[배경정보] QueryDSL은 동적 쿼리 문제 해결, 쉬운 SQL 문법, 문법 오류를 컴파일 시점에 알려주는 특징이 있습니다.";

    //completions : 질답
    public static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5000))
                .setReadTimeout(Duration.ofSeconds(5000))
                .additionalInterceptors(clientHttpRequestInterceptor())
                .build();
    }

    public ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
        return ((request, body, execution) -> {
            RetryTemplate retryTemplate = new RetryTemplate();
            retryTemplate.setRetryPolicy(new SimpleRetryPolicy(2));
            try {
                return retryTemplate.execute(context -> execution.execute(request, body));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }

        });
    }



}
