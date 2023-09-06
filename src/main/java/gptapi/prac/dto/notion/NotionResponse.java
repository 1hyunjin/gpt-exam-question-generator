package gptapi.prac.dto.notion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class NotionResponse {
    private String type;
    private Text text;
    private Annotation annotations;
    @JsonProperty("plain_text")
    private String plainText;
    private String href;

    @Getter
    @Setter
    public static class Text {
        private String content;
        private String link;
    }


    @Getter
    @Setter
    public static class Annotation {
        private Boolean bold;
        private Boolean italic;
        private Boolean strikethrough;
        private Boolean underline;
        private Boolean code;
        private String color;
    }

}
