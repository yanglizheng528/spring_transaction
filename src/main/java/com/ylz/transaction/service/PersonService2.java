package com.ylz.transaction.service;

import com.ylz.transaction.domain.Person;

public interface PersonService2 {
    Person savePersonWithRollBack2(Person person);

    Person savePersonWithoutRollBack2(Person person);


}
