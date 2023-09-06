package gptapi.prac.dto.gpt;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class ChatGptMessage {

    private String role;
    private String content;


}
