package com.ylz.transaction.service;

import com.ylz.transaction.domain.Person;

public interface PersonService {
    Person savePersonWithRollBack(Person person);

    Person savePersonWithoutRollBack(Person person);


}
