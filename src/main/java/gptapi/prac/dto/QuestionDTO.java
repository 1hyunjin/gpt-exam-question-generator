package gptapi.prac.dto;

import lombok.*;

@Getter
@Builder
public class QuestionDTO {

    private String question_number;

    private String q_content;

    private String answer;

    private String background;

}
