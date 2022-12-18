package pl.edu.pk.cosmo.habsatbackend.postsservice.utlis;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.InvalidSortException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.PagingRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Paging<T extends Enum<T>> {
    private final Integer page;
    private final Integer size;
    private final String[] sorts;
    private final Class<T> allowedProperties;

    private Paging(Integer page, Integer size, String[] sorts, Class<T> allowedProperties) {
        this.page = page;
        this.size = size;
        this.sorts = sorts;
        this.allowedProperties = allowedProperties;
    }

    public static <T extends Enum<T>> Paging<T> of(PagingRequest request, Class<T> allowedProperties) {
        return new Paging<>(request.getPage(), request.getSize(), request.getSort(), allowedProperties);
    }

    public Pageable getPageable()  {
        return PageRequest.of(page, size, Sort.by(getOrders()));
    }

    public List<Sort.Order> getOrders() {
        return Arrays.stream(sorts)
                .map(this::mapSortToOrders)
                .collect(Collectors.toList());
    }

    private Sort.Order mapSortToOrders(String sort) {
        String[] parts = sort.split(",");

        String property = parts[0];
        this.validateSortProperty(property);

        Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.DESC;

        return new Sort.Order(direction, property);
    }

    @SneakyThrows
    private void validateSortProperty(String property) {
        try {
            Enum.valueOf(allowedProperties, property);
        } catch (Exception e) {
            String message = String.format("sort value '%s' is not a valid enum value %s", property, Arrays.toString(allowedProperties.getEnumConstants()));
            throw new InvalidSortException(message);
        }
    }
}
