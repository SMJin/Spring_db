# 커넥션 풀과 데이터소스

### Connection Pool
- DB가 tcp/ip 통신을 할 때는 3-way handshake를 이용한 네트워크 동작이 발생한다. 그러나 이렇게 통신을 할 때마다 커넥션을 새로 만드는 것은 복잡하고 시간도 오래 걸린다.
- 이런 문제를 해결하기 위해서 나타난 것이 바로 *Connection Pool(커넥션 풀)* 이다.
- 애플리케이션을 시작하는 시점에 커넥션들을 미리 확보해서 풀에 보관하는 개념이다. 얼마나 보관할지는 보통 default는 10개이다. 이렇게 커넥션 풀에 들어있는 커넥션은 tcp/ip로 db와 이미 커넥션이 연결되어 있는 상태이기 때문에 언제든지 즉시 SQL을 DB에 전달할 수 있다.
- 대표적인 커넥션 풀 오픈소스는 ***commons-dbcp2***, ***tomcat-jdbc pool***, ***HikariCP*** 등이 있다. 이때 스프링부트2.0부터는 기본 커넥션 풀을 hikariCP로 제공하며, 실무에서도 대부분 hikariCP를 사용한다. 이미 성능, 사용의 편리함, 안정성 측명에서 검증이 되었기 때문이다.

### DataSource
- Connection을 얻는 방법은 DriverManager를 통해서 직접 네트워크와 통신하는 방법도 있지만, 이제 Connection Pool을 사용하는 방법도 존재한다는 것을 깨달았다.
- 그런데 DriverManager를 사용하다가 DBCP2 Connection Pool을 연결하고 싶다거나, 커넥션 풀을 사용하다가 또 다른 커넥션 풀인 HikariCP Connection Pool을 연결하는 등 연결 방식을 변경할 때 문제가 발생할 것이다. 왜냐면 연결 방식이 다 다르기 때문이다.
- 그래서 Conneciont을 획득하는 방법을 추상화할 필요성이 나타난 것이다. 그래서 나온 것이 ***DataSource interface***이다.
```java
public interface DataSource {
  Connection getConnection() throws SQLException;
}
```

### DriverManagerDataSource
```java
// DriverManagerDataSource - 항상 새로운 커넥션을 획득
DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

dataSoruce.getConnection();
```
- 설정과 사용의 분리
- > new 를 이용해서 등록할 때는 속성들(URL, USERNAME, PASSWORD)을 전부 등록한다.
- > 그러나 사용할 때는 단순하게 dataSource.getConnection() 만 호출한다. 속성을 따로 또 지정해줄 필요가 없는 것이다.

### HikariDataSource
```java
// 커넥션 풀링
HikariDataSource dataSource = new HikariDataSource();
dataSource.setJdbcUrl(URL);
dataSource.setUsername(USERNAME);
dataSource.setPassword(PASSWORD);
dataSource.setMaximumPoolSize(10);
dataSource.setPoolName("MyPoll");

dataSoruce.getConnection();
```
- 커넥션 풀이 10개까지 차는 모습 log
- > Q. 왜 별도의 쓰레드를 사용해서 커넥션 풀에 커넥션을 채우는 것일까?
- > A. 커넥션 풀에 커넥션을 채우는 것은 오래 걸리는 일이기 때문에 별도의 쓰레드를 사용해서 애플리케이션 실행 시간에 영향을 주지 않기 위함이다.
```log
#커넥션 풀 전용 쓰레드가 커넥션 풀에 커넥션을 10개 채움
[MyPool connection adder] MyPool - Added connection conn0: url=jdbc:h2:..user=SA
[MyPool connection adder] MyPool - Added connection conn1: url=jdbc:h2:..user=SA
[MyPool connection adder] MyPool - Added connection conn2: url=jdbc:h2:..user=SA
[MyPool connection adder] MyPool - Added connection conn3: url=jdbc:h2:..user=SA
[MyPool connection adder] MyPool - Added connection conn4: url=jdbc:h2:..user=SA
...
[MyPool connection adder] MyPool - Added connection conn9: url=jdbc:h2:..user=SA
```
- 커넥션 풀에서 커넥션을 획득하는 모습 log
- > total 커넥션 = 10개, 활동중인(획득된)커넥션 2개, 대기 상태인 커넥션(idle) 8개, 풀에 등록 대기중인 커넥션(waiting) 0개 임을 알 수 있다.
```log
#커넥션 풀에서 커넥션 획득1
ConnectionTest - connection=HikariProxyConnection@446445803 wrapping conn0:
url=jdbc:h2:tcp://localhost/~/test user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection

#커넥션 풀에서 커넥션 획득2
ConnectionTest - connection=HikariProxyConnection@832292933 wrapping conn1:
url=jdbc:h2:tcp://localhost/~/test user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection
MyPool - After adding stats (total=10, active=2, idle=8, waiting=0)
```
