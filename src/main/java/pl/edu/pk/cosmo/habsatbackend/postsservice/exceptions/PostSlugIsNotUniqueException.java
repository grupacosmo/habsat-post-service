package pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason = "slug must be unique")
public class PostSlugIsNotUniqueException extends InternalException {
    public PostSlugIsNotUniqueException() {
        super(Code.POST_SLUG_IS_NOT_UNIQUE);
    }
}
