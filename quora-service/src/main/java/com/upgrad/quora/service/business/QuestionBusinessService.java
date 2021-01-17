package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserBusinessService userBusinessService;

    @Transactional //(propagation = Propagation.REQUIRED)
    public QuestionEntity create(QuestionEntity questionEntity)  {

        QuestionEntity createQuestion = questionDao.createQuestion(questionEntity);

        return createQuestion;
    }

    @Transactional //(propagation = Propagation.REQUIRED)
    public void deleteQuestion(final String uuid,final String authorization)  {
        questionDao.deleteQuestion(uuid);
    }
    @Transactional
    public String editQuestion(final String uuid, final String questionContent,final String accessToken) {
        questionDao.editQuestion(uuid, questionContent);
        return uuid;
    }

    public List<QuestionEntity> getAllQuestions(final String authorization) {

        return questionDao.getAllQuestions();
    }

    public List<QuestionEntity> getAllQuestionsByUser(final UserEntity userId) {
        List<QuestionEntity> questionsList = questionDao.getAllQuestionsByUser(userId);

        return questionsList;
    }

}
