package com.ylz.transaction.dao;

import com.ylz.transaction.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
