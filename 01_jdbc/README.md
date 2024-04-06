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

### PreparedStatment ?
- java.sql.PreparedStatement는 기존 Statement보다 향상된 기능을 제공한다.
- SQL injection을 예방하기 위해서 PreparedStatment를 이용해 ? 로 바인딩 해주어야 한다.
- 인자값으로 전달하기 때문에, 가독성이 좋아진다.
```java
String sql = "insert into member(member_id, money) values (?, ?)";

Connection con = null;
PreparedStatement pstmt = null;

try {
    con = getConnection();
    pstmt = con.prepareStatement(sql);

    // parameter binding
    pstmt.setString(1, member.getMemberId());
    pstmt.setInt(2, member.getMoney());

    // 실행 - 영향받은 row 수를 반환함(int)
    pstmt.executeUpdate();
.
.
.
```

### ResultSet ?
- 보통 select query의 결과가 순서대로 들어가 있는 결과 집합이다.
- 내부에 있는 커서(cursor)를 통해 다음 데이터를 조회하는데, rs.next()를 호출해서 다음 행으로 이동한다. 참고로 최초의 커서는 데이터를 가리키고 있지 않기에 최초 한번은 호출해야 데이터를 조회할 수 있다. 또, rs.next()의 결과가 true 이면 데이터가 있다는 뜻이고, false이면 데이터가 없다는 뜻이다.(보통 마지막 행 데이터까지 모두 조회했다는 의미로 쓰인다.)
- ex) rs.getString("member_id") : 현재 커서가 가리키고 있는 위치의 *member_id* 데이터를 *String* 타입으로 반환한다.
- ex) rs.getInt("money") : *money* 데이터를 *int* 타입으로 반환한다.
```java
String sql = "select * from member where member_id = ?";

Connection con = null;
PreparedStatement pstmt = null;
ResultSet rs = null;

try {
  con = getConnection();
  pstmt = con.prepareStatement(sql);
  pstmt.setString(1, memberId);
  rs = pstmt.executeQuery();

  if (rs.next()) {
    Member member = new Member();
    member.setMemberId(rs.getString("member_id"));
    member.setMoney(rs.getInt("money"));
    return member;
  } else {
    throw new NoSuchElementException("member not found memberId=" + memberId);
  }
.
.
.
```
