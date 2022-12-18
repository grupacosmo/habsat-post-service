package pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidSortException extends InternalException {
    public InvalidSortException(String message) {
        super(message, Code.INVALID_SORT);
    }
}

