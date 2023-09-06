package gptapi.prac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
//@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question_number; //문제 번호

    @Column(columnDefinition = "LONGTEXT")
    private String q_content;

    private String answer;

    @Column(columnDefinition = "LONGTEXT")
    private String background;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    public Question(String content) {
        String[] sections = content.split("\n\n");

        for (String section : sections) {
            System.out.println(section);
            System.out.println("===========");
        }
        this.question_number = extractNumber(sections[0]);
        this.q_content = sections[1];
        this.answer = extractNumber(sections[2]);
        this.background = sections[3];
    }

    //숫자 추출
    private String extractNumber(String section) {

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(section);

        while (matcher.find()) {
            return matcher.group();
        }

        return section; // 숫자가 없을 경우 -1 또는 다른 기본값 반환
    }

}
