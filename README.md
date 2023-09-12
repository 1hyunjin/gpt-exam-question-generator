# gpt-exam-question-generator

## 개요 

대학교 졸업 프로젝트(n2t)의 아이디어를 활용해서 만든 문제 생성 API입니다.

저는 평소 공부한 내용을 Notion에 정리합니다. 그래서 Notion에 정리한 내용을 서비스 페이지에 옮겨 적거나 하는 행위 대신에  notion link를 통해 바로 문제를 생성해주는 서비스가 있으면 좋겠다고 생각이 들어 만들게 되었습니다. 

    

## 개발 기능

1. Notionlink를 통해 Notion PageId를 추출한다. 
2. 추출한 PageId를 통해 페이지의 글 정보를 가져온다. 
3. 글 정보를 [지문] 으로 묶어 객관식 문제를 생성한다.
4. 생성한 문제를 [문제번호], [문제내용], [정답], [배경정보] 로 나누어 DB에 저장한다.

## 핵심 라이브러리 

- RestTemplate : Spring에서 지원하는 Rest 방식 API를 호출할 수 있는 HTTP 클라이언트 라이브러리
    - Notion API, gpt-3.5-turbo API를 헤더 정보 설정 후 API 호출 
    - 응답 결과를 자바 객체로 변환해서 데이터 바인딩 처리 
- Jackson : JSON 데이터 구조를 처리해주는 라이브러리
    - JsonNode를 사용하여 받아온 JSON 데이터를 객체로 변환 후 필요한 데이터 추출하는 데 사용 

## API Spec

| Method | URL             | Request             | Response                   |
|--------|-----------------|---------------------|----------------------------|
| POST   | api/v1/question | notionLink `String` | questionDTO `QuestionDTO`  |

### Request
```
{
    "notionLink" : "https://www.notion.so/leah-space/Notion-API-Test-74f2e95cc0014a50a8557e4b4c28f9be"
}
```

### Response
```
{
    "question_number": "2",
    "q_content": "Spring Security에서 인증과 관련된 방식은 무엇인가요?\n[1] JWT(Json Web Token)\n[2] 세션 & 쿠키\n[3] OAuth2\n[4] SAML(Security Assertion Markup Language)\n[5] Digest Authentication",
    "answer": "2",
    "background": "[배경정보] Spring Security는 기본적으로 세션 & 쿠키 방식으로 인증을 처리합니다."
}
```


## Dependencies

### 1. gpt-3.5-turbo API

**사용 목적** : 문제 생성

**Method** : POST

**URL** :  https://api.openai.com/v1/chat/completions

**Header Setting**

> Content-Type : application/json
> 
> Authorization : ${openai secret API Key}

**System prompt**

```
You're a question taker for the world's leading question taker.
Generate 5 multiple-choice questions, views, answers, and background information for a given fingerprint. The criteria you'll use to create multiple-choice questions include
1. The question should be clear and easy to understand.
2. there should be only one correct answer.
3. the question sentence should be concise and clear.
4. Choose options that allow students to understand and apply key concepts.
Consider these criteria when creating your multiple choice questions. 
For the background information, tell us the underlying paragraph that created the question. 
If the given fingerprint is in Korean, both the question and the example must be in Korean.
```

### 2. Notion API

**사용 목적** : notion 페이지의 글 내용을 하나의 지문으로 생성

**Method** : GET

**URL** :  https://api.notion.com/v1/blocks/:id/children

**Header Setting**

> Authorization : ${Notion API Key}
>
> Notion-Version : 2022-06-28

**Page ID**

notion link의 마지막 32 길이의 문자열

```
notino link : https://www.notion.so/leah-space/Notion-API-c0e156d72f8345e29c5ed771ca0bb194
Page ID : c0e156d72f8345e29c5ed771ca0bb194
```

## Sequence Diagram

![image](https://github.com/1hyunjin/gpt-exam-question-generator/assets/38430900/b6910dd6-44ea-40f6-8743-7c82ac8d7c0d)


## 데이터베이스

### question
| 필드              | 타입           | Key |
|-----------------|--------------|-----|
| id              | bigint       | PK  |
| question_number | varchar(255) | -   |
| q_content       | longtext     | -   |
| answer          | varchar(255) | -   |
| background      | longtext     | -   |
| created_at      | datetime(6)  | -   |


## 기술 스택

- Spring Boot
- Java
- JPA
- MySQL

## 프로젝트 구조 
```
src
   ├─main
   │  ├─java
   │  │  └─gptapi
   │  │      └─prac
   │  │          │  PracApplication.java
   │  │          │
   │  │          ├─config
   │  │          │      Config.java
   │  │          │
   │  │          ├─controller
   │  │          │      QuestionController.java
   │  │          │
   │  │          ├─dto
   │  │          │  │  QuestionDTO.java
   │  │          │  │
   │  │          │  ├─gpt
   │  │          │  │      ChatGptMessage.java
   │  │          │  │      GptRequest.java
   │  │          │  │      GptResponse.java
   │  │          │  │
   │  │          │  └─notion
   │  │          │          NotionLinkRequest.java
   │  │          │          NotionResponse.java
   │  │          │
   │  │          ├─entity
   │  │          │      Question.java
   │  │          │      QuestionRepository.java
   │  │          │
   │  │          ├─service
   │  │          │      QuestionService.java
   │  │          │
   │  │          └─util
   │  │                  StringUtil.java
   │  │
```

