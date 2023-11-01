package GroupThree.bds.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException{
    private final HttpStatus httpStatus;

    public AppException (String message, HttpStatus status){
        super(message);
        this.httpStatus = status;
    }
}
