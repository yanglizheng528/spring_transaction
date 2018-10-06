package com.ylz.transaction.service.impl;

import com.ylz.transaction.BusinessException;
import com.ylz.transaction.dao.PersonRepository;
import com.ylz.transaction.domain.Person;
import com.ylz.transaction.service.PersonService;
import com.ylz.transaction.service.PersonService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("personService2")
public class PersonService2Impl implements PersonService2 {

    @Autowired
    PersonRepository personRepository;
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public Person savePersonWithRollBack2(Person person) {
        Person result = personRepository.save(person);
            throw new BusinessException("数据已存在，会回滚！！！");
        //return result;
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public Person savePersonWithoutRollBack2(Person person) {
        return personRepository.save(person);
        //throw new RuntimeException("出错了。。。。。。");
    }
}
