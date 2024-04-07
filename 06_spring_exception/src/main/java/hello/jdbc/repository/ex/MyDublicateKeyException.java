package hello.jdbc.repository.ex;

public class MyDublicateKeyException extends MyDBException{
    public MyDublicateKeyException() {
    }

    public MyDublicateKeyException(String message) {
        super(message);
    }

    public MyDublicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDublicateKeyException(Throwable cause) {
        super(cause);
    }
}
