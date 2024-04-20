# 스프링 트랜잭션 이해

## 트랜잭션 적용 여부를 확인하는 방법
- TransactionSynchronizationManager.isActualTransactionActive()
  - Boolen 타입을 결과로 반환하는데, true이면 트랜잭션이 적용되어 있다는 뜻이다.
- 다음 로그를 추가하면 호출한 트랜잭션의 시작과 종료를 명확하게 알 수 있다.
```properties
logging.level.org.springframework.transaction.interceptor=TRACE
```

## 트랜잭션은 Proxy를 사용한다.
##### @Transaction이 적용되면 트랜잭션 AOP는 Proxy를 만들어서 스프링 컨테이너에 등록한다. 그러니까 프록시가 대신 빈으로 등록되고, 프록시는 내부의 실제 basicService를 참조한다. 이때 프록시는 본래 객체를 상속해서 만들어지기 때문에 다형성을 활용할 수도 있다. 그에 따라 본래 basicService 대신에 프록시인 BasicService$$SpringCGLIB... 를 주입할 수 있다.
