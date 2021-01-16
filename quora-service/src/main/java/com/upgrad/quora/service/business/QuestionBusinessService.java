package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;

    @Transactional //(propagation = Propagation.REQUIRED)
    public QuestionEntity create(QuestionEntity questionEntity)  {
      /*  if(questionDao.isUserNameExists(userEntity.getUserName())) {
            throw new SignUpRestrictedException("ATHR-001", "User has not signed in");
        }

        if(questionDao.isEmailExists(userEntity.getEmail())) {
            throw new SignUpRestrictedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }*/

        QuestionEntity createQuestion = questionDao.createQuestion(questionEntity);

        return createQuestion;
    }
}
