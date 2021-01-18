package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity class for the answer table in the DB.
 * @author saikatnandi
 */
@Entity
@Table(name = "answer")
@NamedQueries(
        {
                @NamedQuery(name = "getAnswerByUuid", query = "select u from AnswerEntity u where u.uuid=:uuid"),
                @NamedQuery(name = "getAllAnswerByQuestionUuid", query = "select a from AnswerEntity a where a.question=:uuid")

        }
)
public class AnswerEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @Column(name = "ans")
    @Size(max = 255)
    private String answer;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    public Integer getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAnswer() {
        return answer;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public UserEntity getUser() {
        return user;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

}