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
