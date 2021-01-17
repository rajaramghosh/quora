package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

//Entity class for managing question table

@Entity
@Table(name = "question")
@NamedQueries({
        @NamedQuery(name = "getAllQuestions", query = "select q from QuestionEntity q"),
        @NamedQuery(name = "getAllQuestionsByUser", query = "select q from QuestionEntity q where q.userId = :user_id"),
        @NamedQuery(name = "deleteQuestionById", query = "delete QuestionEntity q where q.uuid = :uuid"),
        @NamedQuery(name = "editQuestionById", query = "update QuestionEntity q set q.content = :content where q.uuid = :uuid")
})
public class QuestionEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "CONTENT")
    @NotNull
    @Size(max = 500)
    private String content;

    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }
}
