package pl.edu.pk.cosmo.habsatbackend.postsservice.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.edu.pk.cosmo.habsatbackend.postsservice.converters.PostConverter;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.PostNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.PostSlugIsNotUniqueException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.models.PostSort;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.MediaRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.PostRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.ModifyPostRequest;
import pl.edu.pk.cosmo.habsatbackend.postsservice.PostFactory;
import pl.edu.pk.cosmo.habsatbackend.postsservice.utlis.Paging;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PostServiceUnitTest {
    private final PostRepository postRepositoryMock = mock(PostRepository.class);
    private final MediaRepository mediaRepositoryMock = mock(MediaRepository.class);
    private final PostConverter postConverterMock = mock(PostConverter.class);
    private final ModifyPostRequest modifyPostRequestMock = mock(ModifyPostRequest.class);
    private final Paging<PostSort> pagingMock = mock(Paging.class);
    private final PostService postService = new PostService(postRepositoryMock, mediaRepositoryMock, postConverterMock);
    private final List<Post> listOfPosts = new PostFactory().createMany(2);
    private final Page<Post> paginationOfPosts = new PageImpl<Post>(listOfPosts);

    @AfterEach
    public void afterEach() {
        reset(postRepositoryMock, mediaRepositoryMock, postConverterMock, modifyPostRequestMock);
    }


    @Test
    public void shouldFindAllPostsReturnsAllPostEntities() {
        when(postRepositoryMock.findAll(any(Pageable.class))).thenReturn(paginationOfPosts);
        when(pagingMock.getPageable()).thenReturn(Pageable.unpaged());
        assertThat(postService.findAllPosts(pagingMock)).isEqualTo(paginationOfPosts);
    }

    @Test
    public void shouldFindPostByIdReturnsPostEntity() throws PostNotFoundException {
        when(postRepositoryMock.findById("id")).thenReturn(Optional.of(listOfPosts.get(0)));
        assertThat(postService.findPostById("id")).isEqualTo(listOfPosts.get(0));
        verify(postRepositoryMock, times(1)).findById("id");
    }

    @Test
    public void shouldFindPostByIdThrowsWhenPostNotFound() {
        when(postRepositoryMock.findById("id")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> postService.findPostById("id")).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    public void shouldCreatePostCreatesPostEntity() throws MediaNotFoundException, PostSlugIsNotUniqueException {
        when(postRepositoryMock.existsBySlug(anyString())).thenReturn(false);
        when(postRepositoryMock.save(any(Post.class))).thenReturn(listOfPosts.get(0));
        when(modifyPostRequestMock.getThumbnailId()).thenReturn(null);
        when(postConverterMock.of(any(ModifyPostRequest.class), anyString())).thenReturn(listOfPosts.get(0));
        assertThat(postService.createPost(modifyPostRequestMock)).isEqualTo(listOfPosts.get(0));
    }

    @Test
    public void shouldCreatePostThrowsWhenSlugIsNotUnique() {
        when(modifyPostRequestMock.getSlug()).thenReturn("slug");
        when(postRepositoryMock.existsBySlug(any())).thenReturn(true);
        assertThatThrownBy(() -> postService.createPost(modifyPostRequestMock)).isInstanceOf(PostSlugIsNotUniqueException.class);
    }

    @Test
    public void shouldCreatePostThrowsWhenThumbnailNotFound() {
        when(postRepositoryMock.existsBySlug(any())).thenReturn(false);
        when(modifyPostRequestMock.getThumbnailId()).thenReturn("id");
        when(mediaRepositoryMock.existsById("id")).thenReturn(false);
        assertThatThrownBy(() -> postService.createPost(modifyPostRequestMock)).isInstanceOf(MediaNotFoundException.class);
    }

    @Test
    public void shouldUpdatePostUpdatesPostEntity() throws PostNotFoundException, MediaNotFoundException, PostSlugIsNotUniqueException {
        when(postRepositoryMock.existsBySlugAndIdNot(anyString(), anyString())).thenReturn(false);
        when(postRepositoryMock.findById("id")).thenReturn(Optional.ofNullable(listOfPosts.get(0)));
        when(postRepositoryMock.save(any(Post.class))).thenReturn(listOfPosts.get(0));
        when(modifyPostRequestMock.getThumbnailId()).thenReturn(null);
        when(postConverterMock.of(any(ModifyPostRequest.class), any(Post.class))).thenReturn(listOfPosts.get(0));
        assertThat(postService.updatePost("id", modifyPostRequestMock)).isEqualTo(listOfPosts.get(0));
    }

    @Test
    public void shouldUpdatePostThrowsWhenSlugIsNotUnique() {
        when(modifyPostRequestMock.getSlug()).thenReturn("slug");
        when(postRepositoryMock.existsBySlugAndIdNot("slug", "id")).thenReturn(true);
        assertThatThrownBy(() -> postService.updatePost("id", modifyPostRequestMock)).isInstanceOf(PostSlugIsNotUniqueException.class);
    }

    @Test
    public void shouldUpdatePostThrowsWhenThumbnailNotFound() {
        when(postRepositoryMock.existsBySlugAndIdNot(any(), anyString())).thenReturn(false);
        when(postRepositoryMock.findById("id")).thenReturn(Optional.ofNullable(listOfPosts.get(0)));
        when(mediaRepositoryMock.findById("id")).thenReturn(Optional.empty());
        when(modifyPostRequestMock.getThumbnailId()).thenReturn("id");
        assertThatThrownBy(() -> postService.updatePost("id", modifyPostRequestMock)).isInstanceOf(MediaNotFoundException.class);
    }

    @Test
    public void shouldDeletePostDeletesPostEntity() {
        postService.deletePost("id");
        verify(postRepositoryMock, times(1)).deleteById("id");
    }
}
