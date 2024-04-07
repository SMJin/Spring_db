# 자바 예외 이해

## 예외 계층
![image](https://github.com/SMJin/Spring_db/assets/32761189/bb6609b8-c0df-4fd6-bb12-5a2607dd5b3f)
- Error : 심각하거나 복구 불가능한 예외, 처리할 수 없음.
- Exception : 처리할 수 있는 예외.
- 체크 예외 ? 애플리케이션 로직에서 사용할 수 있는 실질적인 최상위 예외. 단, RuntimeException은 해당하지 않는다. 이들을 ***런타임 예외*** 라고 하는데, RuntimeException과 그 하위 언체크 예외들을 모두 일컫는다.
- 언체크 예외 ? 컴파일러가 체크하지 않는 예외.

## 체크 예외
- Exception 과 그 하위 예외. (단, RuntimeException은 제외)
- 예외를 잡아서 처리하거나, 던지거나 둘중 하나를 필수로 선택해야 한다.
- 예외를 던질 때는, 상위 예외보다는 구체적인 예외로 던져주는 것이 좋다.
- 개발자가 실수로 예외를 누락하지 않도록 컴파일러를 통해 문제를 잡아주는 훌륭한 안전 장치지만, 크게 신경쓰고 싶지 않은 예외까지 챙겨야 한다는 번거로움과 의존관계에 따른 단점이 있다.
#### 문제점
1. **복구 불가능**한 예외
> 대부분의 예외는 복구가 불가능하다. 특히 서비스나 컨트롤러는 예외를 받아봤자 해결할 수 없다. 따라서 이런 문제들은 일관성 있게 공통으로 처리해야 한다. 오류 로그를 남기고 개발자가 해당 오류를 빠르게 인지하는 것이 필요하다. 서블릿 필터, 스프링 인터셉터, 스프링의 ControllerAdvice 를 사용하면 이런 부분을 깔끔하게 공통으로 해결할 수 있다.
2. **의존 관계**에 대한 문제
> 체크 예외는 컨트롤러나 서비스 입장에서 해결할 수 없어도 예외를 선언해야 한다. 특히, throws SQLException, ConnectionException 처럼 java.sql.SqlException 을 의존하는 예외는 더 문제다. 향후 리포지토리를 JDBC 에서 다른 기술로 변경된다면, JPAException 을 의존하도록 전부 변경해야 하기 때문이다. 그렇다고 최상위 예외인 Exception 을 던지는 것도 문제다. 모든 체크 예외를 다 밖으로 던지기 때문이다.
#### Q. 그렇다면 해결책은?
- A. 언체크 예외를 사용하자.

## 언체크 예외
- RuntimeException 과 그 하위 예외.
- 컴파일러가 예외를 체크하지 않는다. 예외를 잡아서 처리하지 않아도 throws를 생략할 수 있다.
- 신경쓰고 싶지 않은 예외를 무시할 수 있으며, 또한 신경쓰고 싶지 않은 예외의 의존관계를 참조하지 않아도 된다. 그러나 개발자가 실수로 예외를 누락할 수 있다는 단점이 있다.
#### 런타임 예외를 사용하자.
- 체크 예외인 SQLException 을 언체크 예외인 RuntimeSQLException으로 변환하고, 체크 예외인 ConnectException 대신에 RuntimeConnectException 을 사용하도록 변환하자.
- 어짜피 예외는 ***대부분 복구 불가능한 예외***이다. 이러한 예외들은 일관성 있게 공통으로 처리해야 한다. 그러니 런타임 예외로 사용하여 별도의 선언(ex. throws) 없이 사용하도록 하자. 별도의 선언을 하지 않으면, 예외를 강제로 ***의존하지 않아도 된다***는 장점도 생긴다.
- 런타임 예외를 사용하면 중간에 기술이 변경되어도 해당 예외를 사용하지 않는 컨트롤러와 서비스에서 코드를 변경하지 않아도 된다. 물론, ***예외를 공통으로 처리***하는 곳에서는 변경이 일어날 수도 있지만, 이 부분만 고치면 된다.

## 런타임 예외는 문서화를 잘해야 한다.
- 코드에 throws 런타임예외를 남겨서 중요한 예외를 인지할 수 있도록 해야한다.
- 예시는 다음과 같다. (JPA EntityManager 의 경우)
```java
/**
 * Make an instance managed and persistent.
 * @param entity entity instance
 * @throws EntityExistsException if the entity already exists.
 * @throws IllegalArgumentException if the instance is not an
 * entity
 * @throws TransactionRequiredException if there is no transaction when
 * invoked on a container-managed entity manager of that is of type
 * <code>PersistenceContextType.TRANSACTION</code>
 */
public void persist(Object entity);
```
- 물론, 런타임 예외도 throws에 선언할 수 있다. 던지는 예외가 명확하고 중요하다면, 명시해놓는 것이 개발자가 IDE를 통해 예외를 확인하기가 편리하다.
- 어짜피 런타임예외는 선언을 한다고 해도 런타임 예외이기 때문에 무시해도 괜찮다.
