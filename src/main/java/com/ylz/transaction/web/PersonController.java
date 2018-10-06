package com.ylz.transaction.web;

import com.ylz.transaction.domain.Person;
import com.ylz.transaction.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "person")
public class PersonController {
    @Autowired
    PersonService personService;

    @RequestMapping(value = "rollback")
    public Person rollback(Person person){
        return personService.savePersonWithRollBack(person);
    }

    @RequestMapping(value = "norollback")
    public Person norollback(Person person){
        return personService.savePersonWithoutRollBack(person);
    }

}
