# 스프링 트랜잭션 이해

## 트랜잭션 적용 여부를 확인하는 방법
- 다음 로그를 추가하면 호출한 트랜잭션의 시작과 종료를 명확하게 알 수 있다.
```properties
logging.level.org.springframework.transaction.interceptor=TRACE
```
- TransactionSynchronizationManager.isActualTransactionActive()
  - Boolen 타입을 결과로 반환하는데, true이면 트랜잭션이 적용되어 있다는 뜻이다.
- TransactionSynchronizationManager.isCurrentTransactionReadOnly()
  - 현재 트랜잭션에 적용된 readOnly 옵션의 값을 반환한다.

## 트랜잭션은 Proxy를 사용한다.
##### @Transaction이 적용되면 트랜잭션 AOP는 Proxy를 만들어서 스프링 컨테이너에 등록한다. 그러니까 프록시가 대신 빈으로 등록되고, 프록시는 내부의 실제 basicService를 참조한다. 이때 프록시는 본래 객체를 상속해서 만들어지기 때문에 다형성을 활용할 수도 있다. 그에 따라 본래 basicService 대신에 프록시인 BasicService$$SpringCGLIB... 를 주입할 수 있다.
##### 그러면 프록시를 사용하는 이유는 뭘까? 왜냐하면, 프록시에 먼저 적용한 다음에 작업이 ***성공적으로 완료되었을 때 Commit***, 즉 실제 객체에 적용해야 하기 때문이다. (***실패시에는 실제 객체에 적용하지 않고 Rollback***하기 위해서)

## 트랜잭션 주의사항
##### 1. 같은 클래스 내에서 @Transactional 이 적용되지 않은 메소드에서 @Transactional 이 적용된 메소드를 호출하면 트랜잭션 적용이 안된다. 
###### 따라서 호출하려면 ***각자 다른 클래스로 선언***해야 한다. 이렇게 되는 이유는, 트랜잭션이 적용되지 않은 메소드는 자신의 본래 객체를 그대로 선언하기 때문이다.
##### 2. 클래스 단위 트랜잭션 (클래스에 @Transactional 을 단 경우)은 ***public 메소드에만*** 적용이 된다.
##### 3. @PostConstruct 와 @Transactional 을 함께 사용하면 트랜잭션이 동작하지 않는다. 대신, => ***@EventListener(ApplicationReadyEvent.class)***과 함께 @Transactional 사용

## Runtime에러(Rollback) vs Checked에러(Commit)
##### 스프링에서는 Runtime Error 발생 시, Rollback 하지만, Checked Exception 발생 시, Commit 한다. 그 이유는 뭘까? 
###### (다만, rollbackFor = MyException.class 를 적용하면 체크 예외도 롤백한다.)
##### 비즈니스 에러 상황 발생 때문.
###### 비즈니스 에러란, 로직 내의 오류가 아니라, 잔고 부족 같이 사용자가 사용할 때 나타나는 에러이다.
