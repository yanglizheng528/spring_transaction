package com.ylz.transaction.service.impl;

import com.ylz.transaction.BusinessException;
import com.ylz.transaction.dao.PersonRepository;
import com.ylz.transaction.domain.Person;
import com.ylz.transaction.service.PersonService;
import com.ylz.transaction.service.PersonService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonRepository personRepository;
    @Autowired
    private PersonService2 personService2;

    // @Transactional注解只能应用到public方法上，如果非public方法应用了@Transactional注解，则该方法不报错，也没有事务功能。

    //------------------------rollbackFor属性---------------------------------
    // 默认情况下，事务将在RuntimeException（非受检异常，unchecked exception）和Error上回滚。

    // 当抛出的异常是rollbackFor属性指定的异常或者是rollbackFor属性指定的异常的子类时，一定会回滚。
    //      当然，抛出其他的RuntimeException和Error，或者其子类也会回滚。

    // rollbackFor属性指定的异常类的实例【最好】是全限定名（如java.lang.IllegalArgumentException.class）

    // rollbackFor属性指定多个值时用英文逗号(,)分隔。格式如下：
    //      @Transactional(noRollbackFor = {com.ylz.transaction.BusinessException.class, java.lang.IllegalArgumentException.class})

    // noRollbackFor属性指定一个值时，格式如下：
    //      @Transactional(noRollbackFor = {com.ylz.transaction.BusinessException.class})  或
    //      @Transactional(noRollbackFor = com.ylz.transaction.BusinessException.class)

    //------------------------rollbackForClassName属性---------------------------------
    // 默认情况下，事务将在RuntimeException（非受检异常，unchecked exception）和Error上回滚。

    // 当抛出的异常是rollbackForClassName属性指定的异常或者是rollbackForClassName属性指定的异常的子类时，一定会回滚。
    //      当然，抛出其他的RuntimeException和Error，或者其子类也会回滚。

    // rollbackForClassName属性指定的异常类的实例【最好】是全限定名（如java.lang.IllegalArgumentException.class）

    // rollbackForClassName属性指定多个值时用英文逗号(,)分隔。格式如下：
    //      @Transactional(rollbackForClassName = {"com.ylz.transaction.BusinessException", "java.lang.IllegalArgumentException"})

    // rollbackForClassName属性指定一个值时，格式如下：
    //      @Transactional(rollbackForClassName = {"com.ylz.transaction.BusinessException"})  或
    //      @Transactional(rollbackForClassName = "com.ylz.transaction.BusinessException")
    @Transactional(rollbackForClassName = {"java.lang.IllegalArgumentException"})
    @Override
    public Person savePersonWithRollBack(Person person) {
        Person result = personRepository.save(person);
        if ("yanglz".equalsIgnoreCase(result.getName())) {
            throw new Error("数据已存在，会回滚！！！");
        }
        return result;
    }

    //------------------------noRollbackFor属性---------------------------------
    // 当抛出的异常是noRollbackFor属性指定的异常或者是noRollbackFor属性指定的异常的子类时，一定不会回滚。

    // noRollbackFor属性指定的异常类的实例【最好】是全限定名（如java.lang.IllegalArgumentException.class）

    // noRollbackFor属性指定多个值时用英文逗号(,)分隔。格式如下：
    //      @Transactional(noRollbackFor = {com.ylz.transaction.BusinessException.class, java.lang.IllegalArgumentException.class})

    // noRollbackFor属性指定一个值时，格式如下：
    //      @Transactional(noRollbackFor = {com.ylz.transaction.BusinessException.class})  或
    //      @Transactional(noRollbackFor = com.ylz.transaction.BusinessException.class)

    //------------------------noRollbackForClassName属性---------------------------------
    // 当抛出的异常是noRollbackForClassName中指定的异常或者是noRollbackForClassName指定的异常的子类时，一定不会回滚。

    // noRollbackForClassName中指定的异常类的名字【最好】是全限定名（如java.lang.IllegalArgumentException）

    // noRollbackForClassName指定多个值时用英文逗号(,)分隔。格式如下：
    //      @Transactional(noRollbackForClassName = {"com.ylz.transaction.BusinessException", "java.lang.IllegalArgumentException"})

    // noRollbackForClassName指定一个值时，格式如下：
    //      @Transactional(noRollbackForClassName = {"com.ylz.transaction.BusinessException"})  或
    //      @Transactional(noRollbackForClassName = "com.ylz.transaction.BusinessException")


    //----------------------------------readOnly属性--------------------------------------------------
    // readOnly属性设置为true，表示是只读事务。数据库将会为只读事务提供一些优化手段，例如Oracle对于只读事务，不启动回滚段，不记录回滚log。

    // 在将事务设置成只读后，相当于将数据库设置成只读数据库，此时若要进行写的操作，会出现错误：
    //      java.sql.SQLException: Connection is read-only. Queries leading to data modification are not allowed......

    // 【使用场景】：
    // 一次执行单条查询语句，则没有必要启用事务支持，数据库默认支持SQL执行期间的读一致性；
    // 一次执行多条查询语句，例如统计查询，报表查询，在这种场景下，多条查询SQL必须保证整体的读一致性，否则，在前条SQL查询之后，后条SQL查询之前，
    //      数据被其他用户改变，则该次整体的统计查询将会出现读数据不一致的状态，此时，应该启用只读事务支持。
    //

    //---------------------------------------timeout属性-------------------------------------------------------
    // 设置事务的超时时间，默认值是-1，单位是【秒】。

    // timeout属性仅适用于Propagation.REQUIRED和Propagation.REQUIRES_NEW，即仅适用于新启动的事务。
    //      如果在设置timeout属性的同时，还设置了propagation属性，且propagation的属性值既不是REQUIRED，
    //      也不是REQUIRES_NEW的话，则timeout属性无效，即当前方法的事务没有超时时间，不会报事务超时异常，也不会回滚。

    // 如果一个方法在timeout指定的时间里没有跑完，并且尚未跑完的部分还有数据库操作的话（包括增删改查操作，注意包括【查询】操作），则事务超时异常（会回滚）：
    //      org.springframework.transaction.TransactionTimedOutException: Transaction timed out: deadline was Sat Sep 29 21:34:02 CST 2018

    // 如果一个方法在timeout指定的时间里没有跑完，但是尚未跑完的部分没有数据库操作的话，则不报事务超时异常（不会回滚）


    //----------------------------------------propagation属性-----------------------------------------------------------------
    // 配置事务传播类型

    // 属性值：
    //      Propagation.REQUIRED        支持当前事务，如果不存在则创建新事务。默认值。
    //          savePersonWithoutRollBack()方法调用了savePersonWithoutRollBack2()方法（该方法的propagation = Propagation.REQUIRED），
    //          如果savePersonWithoutRollBack()方法声明了事务，则savePersonWithoutRollBack2()方法加入该事务，
    //          如果savePersonWithoutRollBack()方法没有声明事务，则savePersonWithoutRollBack2()方法新建一个事务运行。

    //      Propagation.SUPPORTS        支持当前事务，如果不存在则以非事务方式执行。
    //          savePersonWithoutRollBack()方法调用了savePersonWithoutRollBack2()方法（该方法的propagation = Propagation.SUPPORTS），
    //          如果savePersonWithoutRollBack()方法声明了事务，则savePersonWithoutRollBack2()方法以事务方式运行，
    //          如果savePersonWithoutRollBack()方法没有声明事务，则avePersonWithoutRollBack2()方法以非事务方式运行。

    //      Propagation.MANDATORY       必须在事务中运行，否则抛出异常。
    //          savePersonWithoutRollBack()方法调用了savePersonWithoutRollBack2()方法（该方法的propagation = Propagation.MANDATORY），
    //          如果savePersonWithoutRollBack()方法声明了事务，则savePersonWithoutRollBack2()方法以事务方式运行，
    //          如果savePersonWithoutRollBack()方法没有声明事务，则avePersonWithoutRollBack2()方法抛出异常。

    //      Propagation.REQUIRES_NEW    创建一个新事务，并暂停当前事务（如果存在）。
    //          savePersonWithoutRollBack()方法调用了savePersonWithoutRollBack2()方法（该方法的propagation = Propagation.REQUIRES_NEW），
    //          不管savePersonWithoutRollBack()方法是否声明了事务，savePersonWithoutRollBack2()方法都会创建一个新事务，
    //          savePersonWithoutRollBack()方法的事务（如果有的话）暂时挂起。savePersonWithoutRollBack2()方法运行完毕后，
    //          savePersonWithoutRollBack()方法的事务（如果有的话）继续执行。

    //      Propagation.NOT_SUPPORTED   以非事务方式执行，暂停当前事务（如果存在）。
    //          savePersonWithoutRollBack()方法调用了savePersonWithoutRollBack2()方法（该方法的propagation = Propagation.NOT_SUPPORTED），
    //          如果savePersonWithoutRollBack()方法声明了事务，则挂起。直到savePersonWithoutRollBack2()方法以非事务方式运行完毕，
    //          savePersonWithoutRollBack()方法的事务（如果有的话）继续执行。

    //      Propagation.NEVER           以非事务方式执行，如果事务存在则抛出异常。
    //          savePersonWithoutRollBack()方法调用了savePersonWithoutRollBack2()方法（该方法的propagation = Propagation.NEVER），
    //          如果savePersonWithoutRollBack()方法声明了事务，则savePersonWithoutRollBack2()方法报异常，
    //          如果savePersonWithoutRollBack()方法没有声明事务，则savePersonWithoutRollBack2()方法以非事务方式运行。

    //      Propagation.NESTED          如果当前事务存在，则在嵌套事务中执行，其行为类似于PROPAGATION_REQUIRED。
    //          savePersonWithoutRollBack()方法调用了savePersonWithoutRollBack2()方法（该方法的propagation = Propagation.NESTED），
    //          如果savePersonWithoutRollBack()方法声明了事务，则savePersonWithoutRollBack2()方法创建一个新的事务，作为savePersonWithoutRollBack()方法
    //          的子事务运行，如果savePersonWithoutRollBack()方法没有声明事务，则savePersonWithoutRollBack2()方法新建一个事务运行。
    //
    //          【注意：Hibernate/JPA不支持嵌套事务，嵌套事务只在JDBC级别直接支持】。
    //
    //  【PROPAGATION.REQUIRES_NEW和PROPAGATION.NESTED的区别】：
    //      PROPAGATION.REQUIRES_NEW是一个新的事务。
    //      PROPAGATION.NESTED则是外部事务的子事务,如果外部事务commit,则嵌套事务也会被commit,这个规则同样适用于rollback。
    //

    //-----------------------------------------事务的savepoint--------------------------------------------------
    //
    //
    //
    //
    //
    //


    //抛出【org.springframework.transaction.UnexpectedRollbackException: Transaction rolled
    // back because it has been marked as rollback-only】异常的原因：
    // 对于如下方法：
    //      @Transactional
    //      public Result methodA(Param param){
    //          ......
    //          methodB(xxx); // 事务方法
    //          ......
    //          try {
    //            methodC(xxx); // 事务方法，抛出异常
    //          } catch (BusinessException e) {
    //            System.out.println("捕获异常：" + e.getMessage());
    //          }
    //          ......
    //          methodD(xxx); // 事务方法
    //          ......
    //      }
    //
    // 【原因】
    // methodA被调用执行时，methodB()、methodC()以及methodD()被事务管理，只要有异常事件将回滚。
    // 上述场景中存在事务嵌套，如果methodA中有异常出现则事务会直接回滚，
    // 但methodB()、methodC()以及methodD()中有异常只是标记状态为需要回滚，最终在methodA中回滚。
    // 上述场景中methodC()方法有异常，事务被标记为回滚，可是被methodA()捕获了，也就不回滚了，一直执行到最后commit。
    // 在commit时spring会判断回滚标志，若检测到存在回滚标记，则回滚事务并抛出UnexpectedRollbackException异常。
    //

    //----------------------------------------isolation属性-----------------------------------------------------------------
    // 设置事务的隔离级别
    //
    // 属性值：
    //      DEFAULT                 底层数据库的默认隔离级别，数据库管理员设置什么就是什么

    //      READ_UNCOMMITTED        读未提交，会出现脏读、幻读、不可重复读。事务A未提交前，就可被其他事务B读取。如果事务A回滚，则事务B将检索到无效行。

    //      READ_COMMITTED          读已提交，会出现幻读、不可重复读。一个事务提交后才能被其他事务读取到。【sql server的默认隔离级别】。

    //      REPEATABLE_READ         可重复读，会出现幻读（新增或删除操作）。保证在一个事务中多次读取同一个数据时，其值都和事务开始时候的内容是一致。
    //                              【MySql的默认隔离级别】，可通过set transaction isolation level xxx 命令更改。

    //      SERIALIZABLE            串行化，代价最高最可靠的隔离级别，该隔离级别能防止脏读、不可重复读、幻读。
    //
    //
    //
    //
    // 【幻读与不可重复读的区别】
    //      幻读的重点在于【插入与删除】操作，即第二次查询会发现比第一次查询数据变少或者变多了，以至于给人一种幻象一样。
    //      而不可重复读重点在于【修改】操作，即第二次查询会发现查询结果与第一次查询结果不一致，即第一次结果已经不可重现了。
    //

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Person savePersonWithoutRollBack(Person person) {
        Person p = new Person();
        p.setAddress("上海");
        p.setAge(23);
        p.setName("养了");
        personService2.savePersonWithoutRollBack2(p);
        // 创建回滚点
        //Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        System.out.println("--------------------------");
        Person pp = new Person();
        pp.setAddress("上海");
        pp.setAge(25);
        pp.setName("小伙子");
        try {
            personService2.savePersonWithRollBack2(pp);
        } catch (BusinessException e) {
            // 如果事务提交失败 ，回滚到保存点位置
            //TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
            System.out.println("------------------------------------------------");
        }
        System.out.println("--------------------------");
        personRepository.save(person);
        return p;
    }
}
