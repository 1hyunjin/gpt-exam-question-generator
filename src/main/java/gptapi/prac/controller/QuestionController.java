package gptapi.prac.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import gptapi.prac.dto.QuestionDTO;
import gptapi.prac.dto.notion.NotionLinkRequest;
import gptapi.prac.entity.Question;
import gptapi.prac.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("api/v1/question")
    public QuestionDTO createQuestion(@RequestBody NotionLinkRequest req) throws JsonProcessingException {

        Question question = questionService.createQuestion(req);

        QuestionDTO questionDTO = QuestionDTO.builder()
                .question_number(question.getQuestion_number())
                .q_content(question.getQ_content())
                .answer(question.getAnswer())
                .background(question.getBackground())
                .build();

        return questionDTO;
    }
}
