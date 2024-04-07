# 스프링과 문제 해결 - 예외처리, 반복

## Repository 인터페이스를 만들자.
- SQLException 같은 체크 예외를 없애야 한다. 특정 구현 기술에 의존해서는 안된다.
- 이제부터 Repository 구현체들은 이 인터페이스를 상속받아서 구현될 것이다.
```java
public interface MemberRepository {
  Member save(Member member);
  Member findById(String memberId);
  void update(String memberId, int money);
  void delete(String memberId);
}
```

## 체크 에러를 런타임 에러로 변환시켜줄 클래스를 만들자. (데이터 접근 추상화)
- 이제부터 체크 에러가 발생하면 이 런타임에러로 변환해줄 것이다.
- 체크 에러를 런타임 에러로 변환한다는 목적이 핵심이다.
```java
public class MyDBException extends RuntimeException{
    public MyDBException() {
    }
    public MyDBException(String message) {
        super(message);
    }
    public MyDBException(String message, Throwable cause) {
        super(message, cause);
    }
    public MyDBException(Throwable cause) {
        super(cause);
    }
}
```

## 하지만 스프링은 이미.. 다 구현되어 있지롱 (스프링의 예외 추상화 계층)
![image](https://github.com/SMJin/Spring_db/assets/32761189/8d491dc7-f4a2-4bbd-b4e0-62599ace560e)
- 이 모든 예외는 Runtime Exception 이다.
- Transient는 일시적이라는 뜻이므로, 동일한 SQL을 다시 시도했을 때 성공할 가능성이 있다.

## SQLErrorCodeSQLExceptionTranslator
- 해당 클래스를 사용하면 각각의 DB 마다 다른 에러 코드를 연결시켜준다.
```java
SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
// BadSqlGrammerException 문법이 잘못되었다.
DataAccessException resultEx = exTranslator.translate("select", sql, e);

log.info("resultEx", resultEx);
assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
```
#### Q. 스프링은 각 DB에 해당하는 각 에러코드들을 어떻게 관리하고 있는걸까?
- org.springframework.jdbc.support.sql-error-codes.xml 에 비밀이 있다.
- 여기에 에러코드들이 다 매핑되어 있다.
```xml
<bean id="DB2" name="Db2" class="org.springframework.jdbc.support.SQLErrorCodes">
		<property name="databaseProductName">
			<value>DB2*</value>
		</property>
		<property name="badSqlGrammarCodes">
			<value>-007,-029,-097,-104,-109,-115,-128,-199,-204,-206,-301,-408,-441,-491</value>
		</property>
		<property name="duplicateKeyCodes">
			<value>-803</value>
		</property>
		<property name="dataIntegrityViolationCodes">
			<value>-407,-530,-531,-532,-543,-544,-545,-603,-667</value>
		</property>
		<property name="dataAccessResourceFailureCodes">
			<value>-904,-971</value>
		</property>
		<property name="transientDataAccessResourceCodes">
			<value>-1035,-1218,-30080,-30081</value>
		</property>
		<property name="deadlockLoserCodes">
			<value>-911,-913</value>
		</property>
	</bean>
```
