package pl.edu.pk.cosmo.habsatbackend.postsservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PagingRequest {
    @Min(0)
    @Max(100)
    private Integer size = 10;

    @Min(0)
    private Integer page = 0;

    private String[] sort = {};
}
