package pl.edu.pk.cosmo.habsatbackend.postsservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PagingRequest {
    @NotBlank
    private String search;

    @Length(min = 1, max = 100)
    private Integer limit = 10;

    @Min(0)
    private Integer offset = 0;
}
