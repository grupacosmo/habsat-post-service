package pl.edu.pk.cosmo.habsatbackend.postsservice.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.InvalidSortException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.models.PostSort;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.PagingRequest;
import pl.edu.pk.cosmo.habsatbackend.postsservice.utlis.Paging;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class PagingUnitTest {
    private final PagingRequest pagingRequestMock = mock(PagingRequest.class);
    private Paging<PostSort> paging;

    @AfterEach
    public void afterEach() {
        reset(pagingRequestMock);
    }

    @Test
    public void shouldReturnPageable() {
        when(pagingRequestMock.getPage()).thenReturn(0);
        when(pagingRequestMock.getSize()).thenReturn(5);
        when(pagingRequestMock.getSort()).thenReturn(new String[] { "createdAt,desc" });
        paging = Paging.of(pagingRequestMock, PostSort.class);
        Pageable pageable = paging.getPageable();
        assertThat(pageable).isNotNull();
        assertThat(pageable.getOffset()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort()).isNotNull();
    }

    @Test
    public void shouldApplyDefaultSortDirectionWhenNotProvided() {
        when(pagingRequestMock.getPage()).thenReturn(0);
        when(pagingRequestMock.getSize()).thenReturn(5);
        when(pagingRequestMock.getSort()).thenReturn(new String[] { "createdAt" });
        paging = Paging.of(pagingRequestMock, PostSort.class);
        Pageable pageable = paging.getPageable();
        assertThat(pageable).isNotNull();
        assertThat(Objects.requireNonNull(pageable.getSort().getOrderFor("createdAt")).getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    public void shouldThrowWhenUnknownSortPropertyGiven() {
        when(pagingRequestMock.getPage()).thenReturn(0);
        when(pagingRequestMock.getSize()).thenReturn(5);
        when(pagingRequestMock.getSort()).thenReturn(new String[] { "drop database;" });
        paging = Paging.of(pagingRequestMock, PostSort.class);
        assertThatThrownBy(paging::getPageable).isInstanceOf(InvalidSortException.class);
    }
}
