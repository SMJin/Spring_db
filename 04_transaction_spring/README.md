# 스프링과 트랜잭션 문제 해결

## MVC 애플리케이션 구조
1. @Controller : UI 관련 처리 (프레젠테이션 계층)
  - UI와 관련된 처리, 웹 요청/응답, 사용자 요청을 검증, 서블릿과 http 같은 웹기술, Spring MVC 등
2. @Service : 비즈니스 로직 (서비스 계층)
  - 비즈니스 로직, 가급적 특정 기술에 의존하지 않고 ***순수 자바 코드***로 작성
3. @Repository : DB 접근 처리 (데이터 접근 계층)
  - 실제 데이터베이스에 접근하는 코드이며, JDBC, JPA, File, Redis, Mongo 등

## 순수한 서비스 계층 ?
- 핵심은, 시간이 흘러서 UI와 DB가 바뀌어도 비즈니스 로직은 최대한 ***변경없이*** 유지되어야 한다는 것이다. 그러기 위해서는 서비스 계층을 ***특정 기술에 종속되지 않게*** 개발해야 한다.
- 예를 들어, 다음과 같은 상황들이 발생되면 안된다.
1. JDBC 구현 기술이 서비스 계층에 누수되면 안된다.
  - jdbc 관련 라이브러리를 import 해서 사용하면 안된다. 데이터 접근 계층에 JDBC 코드가 다 있어야 하며, 물론 *데이터 접근 계층의 구현 기술이 변경*될 수 있으니 데이터 접근 계층은 ***인터페이스***를 통해 접근하는 것이 좋다.
2. 예외가 누수되면 안된다.
  - 예를 들어 예외 중 하나인 SQLException 이 서비스 계층에서 호출되고 있으면 안된다. 즉, 데이터 접근 계층의 JDBC 구현 기술 예외가 서비스 계층으로 전파되는 일이 있어서는 안된다. 추후에 다른 데이터 접근 기술을 사용하면, JDBC 전용 예외 기술인 SQLException도 다른 접근 기술에 맞는 예외로 변경해야 하기 때문이다.
3. 코드 반복 문제
 - try, catch, finally ... 등이 아니더라도 비슷한 코드의 반복은 줄이는 것이 좋다.

## TransactionManager :: 트랜잭션 추상화
- 비즈니스 로직이 구체적인 JDBC 트랜잭션 관리 구현체에 의존하는 것이 아닌 추상화된 DB 트랜잭션 관리 인터페이스에 의존하면 의존성을 분리할 수 있다.
- 스프링 트랜잭션 추상화의 핵심은 ***PlatformTransactionManager*** 인터페이스이다.
> org.springframework.transaction.PlatformTransactionManager (interface)
```java
package org.springframework.transaction;
public interface PlatformTransactionManager extends TransactionManager {

  // 트랜잭션을 시작한다.
  TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;

  // 트랜잭션을 커밋한다.
  void commit(TransactionStatus status) throws TransactionException;

  // 트랜잭션을 롤백한다.
  void rollback(TransactionStatus status) throws TransactionException;
}
```
- 구체적인 구현체로는 다음과 같다.
1. DataSourceTransactionManager (JDBC 트랜잭션 관리)
2. JpaTransactionManager (JPA 트랜잭션 관리)
3. HibernateTransactionManager (하이버네이트 트랜잭션 관리)
4. EtcTransactionManager (기타 트랜잭션 관리)

## 트랜잭션 동기화 (리소스 동기화 - 같은 커넥션을 유지하는 방법)
- 스프링은 *트랜잭션 동기화 매니저*를 제공한다. 이것은 ***쓰레드 로컬(ThreadLocal)*** 을 사용해서 커넥션을 동기화해준다.
- 다음 트랜잭션 동기화 매니저 클래스를 열어보면 쓰레드 로컬을 사용하는 것을 확인할 수 있다.
> org.springframework.transaction.support.TransactionSynchronizationManager
