package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional //(propagation = Propagation.REQUIRED)
    public QuestionEntity create(QuestionEntity questionEntity)  {

        QuestionEntity createQuestion = questionDao.createQuestion(questionEntity);

        return createQuestion;
    }


    public List<QuestionEntity> getAllQuestions(final String authorization) {
        System.out.println("Inside QBS getAllQuestionsByUser");

        UserAuthEntity user = userDao.getUserAuthByToken(authorization);

        return questionDao.getAllQuestions();

        //return getAllQuestion;
    }


}
