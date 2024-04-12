# 커넥션 풀과 데이터소스

### Connection Pool
- DB가 tcp/ip 통신을 할 때는 3-way handshake를 이용한 네트워크 동작이 발생한다. 그러나 이렇게 통신을 할 때마다 커넥션을 새로 만드는 것은 복잡하고 시간도 오래 걸린다.
- 이런 문제를 해결하기 위해서 나타난 것이 바로 *Connection Pool(커넥션 풀)* 이다.
- 애플리케이션을 시작하는 시점에 커넥션들을 미리 확보해서 풀에 보관하는 개념이다. 얼마나 보관할지는 보통 default는 10개이다. 이렇게 커넥션 풀에 들어있는 커넥션은 tcp/ip로 db와 이미 커넥션이 연결되어 있는 상태이기 때문에 언제든지 즉시 SQL을 DB에 전달할 수 있다.
- 대표적인 커넥션 풀 오픈소스는 ***commons-dbcp2***, ***tomcat-jdbc pool***, ***HikariCP*** 등이 있다. 이때 스프링부트2.0부터는 기본 커넥션 풀을 hikariCP로 제공하며, 실무에서도 대부분 hikariCP를 사용한다. 이미 성능, 사용의 편리함, 안정성 측명에서 검증이 되었기 때문이다.

### DataSource
- Connection을 얻는 방법은 DriverManager를 통해서 직접 네트워크와 통신하는 방법도 있지만, 이제 Connection Pool을 사용하는 방법도 존재한다는 것을 깨달았다.
- 그런데 DriverManager를 사용하다가 DBCP2 Connection Pool을 연결하고 싶다거나, 커넥션 풀을 사용하다가 또 다른 커넥션 풀인 HikariCP Connection Pool을 연결하는 등 연결 방식을 변경할 때 문제가 발생할 것이다. 왜냐면 연결 방식이 다 다르기 때문이다.
- 그래서 Connection 획득하는 방법을 추상화할 필요성이 나타난 것이다. 그래서 나온 것이 ***DataSource interface***이다.
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

### DI : Dependency Injection
- DriverManagerDataSource HikariDataSource 로 변경해도 MemberRepositoryV1 의 코드는 전혀 변
경하지 않아도 된다. MemberRepositoryV1 는 DataSource 인터페이스에만 의존하기 때문이다. 이것이 DataSource 를 사용하는 장점이다.(DI + OCP)

### Dependency Injection (의존성 주입)
- 내부가 아니라 외부에서 객체를 생성해서 주입시켜주는 것을 말한다.
- 상위 계층은 추상화해놓고, 하위 계층에서는 구현을 하는 방식으로 의존성을 분리하여 독립시키는 것이 바로 의존성 분리이다.
- 이 때 나온 개념이 바로 ***IoC :: Inversion Of Control (제어의 역전)*** 인데, 상위의 계층이 하위에 계층에 의존하여 결합도가 높은 코드를 말한다. 예시는 다음과 같다.
```java
public class Car {
  private HyunDai hyundai;

  public Car() {
    // 차 브랜드를 현대에서 기아로 바꾸려면 코드 자체를 변경해야 한다.
    // new Kia(); 로 직접 코드를 변경해야 한다.
    this.hyundai = new HyunDai();
  }
}
```
- 아래 예시는 의존성 주입을 통해 분리시킨 예시이다.
```java
public interface CarBrand { }

public class HyunDai implements CarBrand { }
public class Kia implements CarBrand { }

public class Car {
  private CarBrand brand;

  public Car(CarBrand brand) {
    // 이렇게 하면 Car를 생성할 때 브랜드를 외부에서 주입시켜줄 수 있다.
    // 외부에서 Car를 호출할때, 현대를 호출하려면 new Car(HyunDai) 를 하면 되고, 기아를 호출하려면 new Car(Kia)를 하면 된다.
    this.brand = brand;
  }
}
```

### OCP : Open - Close Principle
- 객체지향 설계 5원칙 SOLID 중, O 에 해당하는 원칙이다.
- **확장에 대해서는 개방적(Open)** 이고, **수정에 대해서는 폐쇄적(Closed)** 이어야 한다는 의미로 정의된다.
- 예를들어, CarBrand 에서 또 다른 브랜드인 도요타가 추가된다고 해서, Car 클래스나 CarBrand 인터페이스가 변경되지 않는다.(수정에 폐쇄적) 단지, 도요타 클래스를 추가해주면 된다.(확장에 개방적) 다음과 같이 말이다.
```java
public class Toyota implements CarBrand { }
```
