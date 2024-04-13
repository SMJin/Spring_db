# 데이터 접근 기술 - JPA

## JPA, Java Persistence API
- 자바 진영의 ORM 표준.
- SQL 중심적인 개발이 아닌, 객체 중심의 개발을 가능케 함 (Java Collection 처럼)
- JDBC API를 사용하여, ResultSet을 반환해주고, 패러다임의 불일치도 해결해준다.
- JPA는 인터페이스의 모음이고, Hibernate, EclipseLink, DataNucleus 등과 같은 구현체가 존재한다.

## ORM, Object-Relational Mapping (객체-관계 매핑)
- 객체는 객체대로 설계하고, RDB는 관계형 DB대로 설계하고, ORM 프레임워크가 중간에서 매핑해주는 형식이다.
