# 데이터 접근기술 - Jdbc Template

## Template Callback Pattern (템플릿 콜백 패턴)
- 스프링 프레임워크에서 DI(Dependency injection) 의존성 주입에서 사용하는 특별한 전략 패턴 (GoF 디자인패턴은 아닌 셈)
- 자바스크립트에서 함수란 객체이며, 이때 콜백이란, 하나의 객체를 다른 객체의 매개변수로 넘겨주는 것을 말한다.
> 즉, 자바스크립트에서 콜백 함수란, 인자로 대입되는 함수를 말한다.
- 전략 패턴에 스프링만의 DI를 엮어서, 구체적인 객체를 설정할 때 익명함수를 이용해 콜백 함수처럼 객체를 설정하는 것을 말한다.
> 예를들어 Animal interface 가 있고 구체적인 객체로 Dog, Cat 이 있을 때 다음 예시를 보자.
```java
interface AnimalStrategy {
  String speak();
}

class AnimalTemplate {
  String speak(AnimalStrategy animal) {
    String result = animal.speak();
    return result;
  }
}

public class Client {
  public static void main(String[] args) {
      AnimalTemplate anitem = new AnimalTemplate();
  
      String result = anitem.speak(new AnimalStrategy() {
          // callback
          @Override
          public String speak() {
              return "야옹~";
          }
      });
      System.out.println(result); // 야옹~

      result = anitem.speak(new AnimalStrategy() {
        	// callback
            @Override
            public String speak() {
                return "멍멍!";
            }
        });
        System.out.println(result); // 멍멍!
  }
}
```

## Java 8 : 익명클래스 (Anonymous Class)
- 이름이 없다는 것, 기억할 필요가 없다는 것, 즉 임시로 쓰이고 버려지는 클래스라는 뜻이다.
- 클래스나 추상클래스 등을 파라미터로 가져와서, 구체적인 구현은 파라미터 안에서 하는 것이다.

## Java 8 : 람다 (Lambda)
- 일종의 코드를 줄이는 전략 중 하나이다.
- 흔하게 반복되는 코드를 줄여서 간략하게 줄이는 것이 핵심이다.
```java
// 람다 적용 전
String result = anitem.speak(new AnimalStrategy() {
    // callback
    @Override
    public String speak() {
        return "야옹~";
    }
});

// 람다 적용 후
String result = anitem.speak(() -> {
    return "야옹~";
});
```
- 단, 람다 표현식에는 제한이 있다.
1. 인터페이스여야 하며,
2. 인터페이스에는 하나의 추상 메서드만 선언되어야 한다.

## JdbcTemplate.class 에서 적용한 익명클래스 + 람다
- [참고](https://www.inflearn.com/questions/749503/jdbctemplateitemrepositoryv1-%EC%A7%88%EB%AC%B8)
- JdbcTemplate 에서는 결과 객체를 담는 ResultSet에 익명클래스를 적용했다.
- 익명클래스는 함수처럼 담긴다는 의미로, @FunctionalInterface를 추가해주어야 한다.
- ResultSet 객체를 RowMapper interface에 담았다.
```java
@FunctionalInterface
public interface RowMapper<T> {
    @Nullable
    T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
```
- 사용법은 다음과 같다.
```java
private RowMapper<Item> itemRowMapper() {
    return (rs, rowNum) -> {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setItemName(rs.getString("item_name"));
        item.setPrice(rs.getInt("price"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    };
}
```
- 람다를 풀어쓰면 다음과 같다.
```java
private RowMapper<Item> itemRowMapper() {
    return new RowMapper<Item>() {
        public Item mapRow(ResultSet rs, int rowNum) {
          Item item = new Item();
          item.setId(rs.getLong("id"));
          item.setItemName(rs.getString("item_name"));
          item.setPrice(rs.getInt("price"));
          item.setQuantity(rs.getInt("quantity"));
          return item;
      };
  }
}
```

## JdbcTemplate 의 단점
- 동적 쿼리 구현이 상당히 복잡하다.
