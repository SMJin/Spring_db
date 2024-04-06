# JDBC 이해

##### 추가해야할 라이브러리
- JDBC API
- H2 Database

### JDBC(Java Database Connectivity)
- Java 기반 애플리케이션에서 DB에 접근할 때, JDBC API를 사용하여 JDBC 표준 인터페이스로 정의되여 연동하는 방식이다.
- java.sql.Connection (연결), java.sql.Statement (SQL을 담은 내용), java.sql.ResultSet (SQL 요청 응답)

### JDBC 를 편리하게 사용하는 두 가지 방법

1. SQL Mapper
  - SQL 응답 결과를 객체로 편리하게 변환해주며, SQL의 반복 코드를 제거해준다.
  - 다만, 개발자가 SQL을 직접 작성해야한다. 하지만 SQL만 작성할 줄 알면 금방 배워서 사용할 수 있다.
  - 대표적인 기술으로는, ***스프링 JdvcTemplate*** 와 ***MyBatis*** 가 있다.

2. ORM 기술 (Object-Relational Mapping)
  - 객체를 관계형 데이터베이스 테이블과 매핑해주는 기술이며, SQL을 직접 작성하지도 않는다.
  - 대표적인 기술으로는, ***JPA***, ***Hibernate(하이버네이트)***, ***EclipseLink(이클립스링크)*** 가 있다. JPA는 자바 진영의 ORM 표준 인터페이스이며, 이를 구현한 것으로 하이버네이트와 이클립스링크 등의 구현기술이 있는 것이다.



### JDBC DriverManager에 대하여
- 여기, Connection interface가 있다. java.sql.Connection은 표준 커넥션 인터페이스이다.
- H2 dababase이든, MySql이든, 각 DB들은 JDBC에 연결되기 위한 어댑터같은 구현체들이 있다. H2의 경우 Connection interface의 구현체로 H2 Conenction이 존재하며, 구체적으로 org.h2.jdbc.JdbcConnection 이다.
- 이때, DriverManager는 라이브러리에 등록된 DB 드라이버들을 관리하고 Connection 구현체를 획득하는 일을 담당한다.
- DriverManager.getConnection() 을 호출하면 드라이버 목록을 스캔하고 알맞은 DB 커넥션을 가져온다.
