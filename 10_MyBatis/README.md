# 데이터 접근 기술 - MyBatis

## MyBatis 란?
- JdbcTemplate보다 더 많은 기능을 제공하는 SQL Mapper
- JdbcTemplate이 제공하는 대부분의 기능을 제공한다.
- SQL을 XML에 편리하게 작성할 수 있다.
- 동적 쿼리를 매우 편리하게 작성할 수 있다.
- 다만, Spring 에서 공식적으로 관리하지는 않기 때문에, 라이브러리를 불러올 때도 정확한 버전 명을 작성해주어야 한다.
- [공식 사이트](https://mybatis.org/mybatis-3/ko/index.html)

## MyBatis 설정하기
### 1. 라이브러리 추가해주기
```gradle
//MyBatis 추가
implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.0' 
```
- 다음과 같은 라이브러리가 추가된다.
###### mybatis-spring-boot-starter : MyBatis를 스프링 부트에서 편리하게 사용할 수 있게 시작하는 라이브러리
###### mybatis-spring-boot-autoconfigure : MyBatis와 스프링 부트 설정 라이브러리
###### mybatis-spring : MyBatis와 스프링을 연동하는 라이브러리
###### mybatis : MyBatis 라이브러리

### 2. application.properties (src/java/resource, src/test/resource 각각 따로 둘다 지정해주어야 함)
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

## MyBatis 실행하기
### 3. @Mapper interface 생성
### 4. xml 파일 생성 (단, xml은 java 파일이 아니기 때문에 resources/ 하위에 만들어주어야 한다.
- namespace를 연결할 Mapper interface 로 지정해준다.
- 단, 파일 경로는 연결된 Mapper interface가 있는 경로랑 동일하게 맞춰주어야 하는데, 이걸 원하지 않는다면, application.properties 에 다음과 같이 설정하면 된다.
```properties
mybatis.mapper-locations=classpath:mapper/**/*.xml
```

## MyBatis 분석하기
#### 1. 파라미터는 #{} 문법을 사용하면 된다. #{} 문법을 사용하면 ***PreparedStatement*** 를 사용한다. JDBC의 ? 를 치환한다 생각하면 된다.
#### 2.  useGeneratedKeys="true" keyProperty="id" 이렇게 사용하면 pk값에 대해 auto incresed 처럼 적용된다.
> useGeneratedKeys 는 데이터베이스가 키를 생성해 주는 IDENTITY 전략일 때 사용한다. keyProperty는 생성되는 키의 속성 이름을 지정한다. Insert가 끝나면 item 객체의 id 속성에 생성된 값이 입력된다.
```java
void save(Item item);
```
```xml
<insert id="save" useGeneratedKeys="true" keyProperty="id">
 insert into item (item_name, price, quantity)
 values (#{itemName}, #{price}, #{quantity})
</insert>
```
#### 3. @Param
```java
import org.apache.ibatis.annotations.Param;
void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto updateParam);
```
```xml
<update id="update">
 update item
   set item_name=#{updateParam.itemName},
   price=#{updateParam.price},
   quantity=#{updateParam.quantity}
 where id = #{id}
</update>
```
#### 4. application.properties 에 mybatis.type-aliasespackage=hello.itemservice.domain 속성을 지정
- 덕분에 모든 패키지 명을 다 적지는 않아도, 결과를 Item 객체에 매핑한다.
```java
Optional<Item> findById(Long id);
```
```xml
<!-- resultType=hello.itemservice.domain.Item 으로 풀 패키지명을 안적어줘도 된다. properties에 패키지 설정했기에. -->
<select id="findById" resultType="Item">
 select id, item_name, price, quantity
 from item
 where id = #{id}
</select>
```
#### 5. application.properties 에 mybatis.configuration.map-underscore-to-camel-case=true 속성을 지정
- 덕분에 언더스코어를 카멜 표기법으로 자동으로 처리해준다. ( item_name itemName )
#### 6. <if test="...">, 동적쿼리.
#### 7. xml 특수문자
```text
< : &lt;
> : &gt;
& : &amp;
```
#### 8. CDATA 구문 문법을 사용하면 특수문자를 사용할 수 있다.
```xml
<if test="maxPrice != null">
    and price &lt;= #{maxPrice}
</if>

<!-- CDATA 적용 문법 -->
<if test="maxPrice != null">
  <![CDATA[
  and price <= #{maxPrice}
  ]]>
</if>
```
#### 9. MyBatis의 동적쿼리 문법은 **OGNL** 문법이다.
#### 10. 문자열 대체(String Substitution)
- 파라미터 바인딩이 아니라 문자 그대로를 처리하고 싶은 경우도 있다. 이때는 ${} 를 사용하면 된다.
- 하지만 보안에 취약할 수 있다. SQL Injection에 취약하다.

## MyBatis 의 구현체는 어디있는가? 어떻게 동작하는가?
1. 애플리케이션 로딩 시점에 MyBatis 스프링 연동 모듈은 @Mapper 가 붙어있는 인터페이스를 조사한다.
2. 해당 인터페이스가 발견되면 동적 프록시 기술을 사용해서 ItemMapper 인터페이스의 프록시 구현체를 만든다.
3. 생성된 구현체를 스프링 빈으로 등록한다.
