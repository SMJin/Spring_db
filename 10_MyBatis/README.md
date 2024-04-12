# 데이터 접근 기술 - MyBatis

## MyBatis 란?
- JdbcTemplate보다 더 많은 기능을 제공하는 SQL Mapper
- JdbcTemplate이 제공하는 대부분의 기능을 제공한다.
- SQL을 XML에 편리하게 작성할 수 있다.
- 동적 쿼리를 매우 편리하게 작성할 수 있다.
- 다만, Spring 에서 공식적으로 관리하지는 않기 때문에, 라이브러리를 불러올 때도 정확한 버전 명을 작성해주어야 한다.
- [공식 사이트](https://mybatis.org/mybatis-3/ko/index.html)

## MyBatis 설정하기
#### 1. 라이브러리 추가해주기
```gradle
//MyBatis 추가
implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.0' 
```
- 다음과 같은 라이브러리가 추가된다.
###### mybatis-spring-boot-starter : MyBatis를 스프링 부트에서 편리하게 사용할 수 있게 시작하는 라이브러리
###### mybatis-spring-boot-autoconfigure : MyBatis와 스프링 부트 설정 라이브러리
###### mybatis-spring : MyBatis와 스프링을 연동하는 라이브러리
###### mybatis : MyBatis 라이브러리
#### 2. application.properties (src/java/resource, src/test/resource 각각 따로 둘다 지정해주어야 함)
```properties
# MyBatis
mybatis.type-aliases-package=hello.itemservice.domain
mybatis.configuration.map-underscore-to-camel-case=true
logging.level.hello.itemservice.repository.mybatis=trace
```
- mybatis.type-aliases-package
###### 마이바티스에서 타입 정보를 사용할 때는 패키지 이름을 적어주어야 하는데, 여기에 명시하면 패키지 이름을 생략할 수 있다.
###### 지정한 패키지와 그 하위 패키지가 자동으로 인식된다.
###### 여러 위치를 지정하려면 , , ; 로 구분하면 된다.
- mybatis.configuration.map-underscore-to-camel-case
###### JdbcTemplate의 BeanPropertyRowMapper 에서 처럼 언더바를 카멜로 자동 변경해주는 기능을 활성화 한다. 바로 다음에 설명하는 관례의 불일치 내용을 참고하자.
- logging.level.hello.itemservice.repository.mybatis=trace
###### MyBatis에서 실행되는 쿼리 로그를 확인할 수 있다.
