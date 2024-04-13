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

## JPA 사용하기
#### @Entity 설정
- JPA가 도메인 클래스를 객체로 인식할 수 있도록.
#### @GeneratedValue(strategy = GenerationType.IDENTITY)
- PK 생성 값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용한다. 예) MySQL auto increment
#### @Column(name = "item_name", length = 10)
- 객체는 itemName, DB에서는 item_name 임을 알려준다. 이렇게 적어주면 자동 매핑이 된다. 이름이 같은 경우에는 생략할 수 있다. 사실 근데 이경우는 없어도 된다. 언더스코어는 카멜케이스로 자동 변환되기 때문이다.
- 참고로, 도메인에 이렇게 @ 로 지정해준 내용을 토대로 create table도 가능하다. 그럴 때 length=10 정보도 쓰인다.
#### JPA는 public, protected 의 생성자가 필수이다.
#### EntityManager 를 DI 해주어야 한다. 
- JPA의 모든 엔티티 매니저를 통해 이루어지며, 엔티티 매니저는 내부에 datasource를 가지고 있고, db에 접근할 수 있다.
#### @Transactional
- JPA의 모든 데이터 변경은 트랜잭션 안에서 이루어진다. (조회 제외) 보통은 비즈니스 로직이 담겨있는 서비스 계층에 트랜잭션을 걸어준다.
#### <insert의 경우> entityManager.persist(item)
- JPA에서 객체를 테이블에 저장할 때는 엔티티 매니저가 제공하는 persist() 메서드를 사용하면 된다.
#### <update의 경우> entityManager.update() 같은건 없다.
- JPA는 트랜잭션이 commit 되는 시점에, 변경된 엔티티가 있는지 확인한다. 특정 엔티티가 변경된 경우에 update sql을 실행한다. (영속성 컨텍스트)
#### <select의 경우(단건 조회)> entityManager.find(Item.class, itemId)
#### <select의 경우(다건 조회)> JPQL : Java Persistence Query Language
- 그러나 얘도 복잡하다, 별로임. 이런 동적 쿼리의 경우 Querydsl 이라는 기술을 사용하자.

## JPA의 예외 변환
- JPA의 경우 JPA 예외가 발생하는데, PersistenceException 과 그 하위 예외를 발생시킨다.
  - 추가로 JPA는 IllegalStateException , IllegalArgumentException 을 발생시킬 수 있다.
- 스프링이 JPA 예외를 스프링 예외 추상화(DataAccessException)으로 변환하는 방법은?
  - 비밀은 @Repository에 있다. @Repository 가 하는 두 가지 기능은 ...
    - 첫째, 컴포넌트 스캔의 대상으로 만든다.
    - 둘째, ***예외 변환 AOP의 적용 대상***으로 만든다. 그래서 스프링과 JPA를 함께 사용하는 경우에, 스프링은 JPA 예외 변환기(PersistenceExceptionTranslator)를 등록한다. 예외 변환 AOP 프록시는 JPA 관련 예외가 발생하면 JPA 예외 변환기를 통해 발생한 예외를 스프링 데이터 접근 예외로 변환한다.
