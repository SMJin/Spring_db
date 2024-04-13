# 데이터 접근 기술 - JPA

## JPA, Java Persistence API
- 자바 진영의 ORM 표준.
- SQL 중심적인 개발이 아닌, 객체 중심의 개발을 가능케 함 (Java Collection 처럼)
- JDBC API를 사용하여, ResultSet을 반환해주고, 패러다임의 불일치도 해결해준다.
- JPA는 인터페이스의 모음이고, Hibernate, EclipseLink, DataNucleus 등과 같은 구현체가 존재한다.

## ORM, Object-Relational Mapping (객체-관계 매핑)
- 객체는 객체대로 설계하고, RDB는 관계형 DB대로 설계하고, ORM 프레임워크가 중간에서 매핑해주는 형식이다.

## JPA 설정하기
1. 라이브러리 추가
```gradle
//JPA, 스프링 데이터 JPA 추가
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
```
- 다음과 같은 라이브러리가 추가된다.
  - hibernate-core : JPA 구현체인 하이버네이트 라이브러리
  - jakarta.persistence-api : JPA 인터페이스
  - spring-data-jpa : 스프링 데이터 JPA 라이브러리

2. log 추가
```properties
#JPA log
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```
- org.hibernate.SQL=DEBUG : 하이버네이트가 생성하고 실행하는 SQL을 확인할 수 있다.
- org.hibernate.orm.jdbc.bind=TRACE : SQL에 바인딩 되는 파라미터를 확인할 수 있다.
- spring.jpa.show-sql=true : 이 설정은 logger가 아닌 System.out 콘솔을 통해 SQL을 출력되도록 한다. (추천하지 않는다)
