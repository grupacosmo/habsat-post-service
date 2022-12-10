package pl.edu.pk.cosmo.habsatbackend.postsservice.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.edu.pk.cosmo.habsatbackend.postsservice.converters.PostConverter;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.PostNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.PostSlugIsNotUnique;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.MediaRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.PostRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.ModifyPostRequest;
import pl.edu.pk.cosmo.habsatbackend.postsservice.utils.factories.PostFactory;

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
    private final PostService postService = new PostService(postRepositoryMock, mediaRepositoryMock, postConverterMock);
    private final List<Post> listOfPostEntities = new PostFactory().createMany(2);

    @AfterEach
    public void afterEach() {
        reset(postRepositoryMock, mediaRepositoryMock, postConverterMock, modifyPostRequestMock);
    }


    @Test
    public void shouldFindAllPostsReturnsAllPostEntities() {
        when(postRepositoryMock.findAllByOrderById()).thenReturn(listOfPostEntities);
        assertThat(postService.findAllPosts()).isEqualTo(listOfPostEntities);
    }

    @Test
    public void shouldFindPostByIdReturnsPostEntity() throws PostNotFoundException {
        when(postRepositoryMock.findById("id")).thenReturn(Optional.of(listOfPostEntities.get(0)));
        assertThat(postService.findPostById("id")).isEqualTo(listOfPostEntities.get(0));
        verify(postRepositoryMock, times(1)).findById("id");
    }

    @Test
    public void shouldFindPostByIdThrowsWhenPostNotFound() {
        when(postRepositoryMock.findById("id")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> postService.findPostById("id")).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    public void shouldCreatePostCreatesPostEntity() throws MediaNotFoundException, PostSlugIsNotUnique {
        when(postRepositoryMock.existsBySlug(anyString())).thenReturn(false);
        when(postRepositoryMock.save(any(Post.class))).thenReturn(listOfPostEntities.get(0));
        when(modifyPostRequestMock.getThumbnailId()).thenReturn(null);
        when(postConverterMock.of(any(ModifyPostRequest.class), anyString())).thenReturn(listOfPostEntities.get(0));
        assertThat(postService.createPost(modifyPostRequestMock)).isEqualTo(listOfPostEntities.get(0));
    }

    @Test
    public void shouldCreatePostThrowsWhenSlugIsNotUnique() {
        when(modifyPostRequestMock.getSlug()).thenReturn("slug");
        when(postRepositoryMock.existsBySlug(any())).thenReturn(true);
        assertThatThrownBy(() -> postService.createPost(modifyPostRequestMock)).isInstanceOf(PostSlugIsNotUnique.class);
    }

    @Test
    public void shouldCreatePostThrowsWhenThumbnailNotFound() {
        when(postRepositoryMock.existsBySlug(any())).thenReturn(false);
        when(modifyPostRequestMock.getThumbnailId()).thenReturn("id");
        when(mediaRepositoryMock.existsById("id")).thenReturn(false);
        assertThatThrownBy(() -> postService.createPost(modifyPostRequestMock)).isInstanceOf(MediaNotFoundException.class);
    }

    @Test
    public void shouldUpdatePostUpdatesPostEntity() throws PostNotFoundException, MediaNotFoundException, PostSlugIsNotUnique {
        when(postRepositoryMock.existsBySlugAndIdNot(anyString(), anyString())).thenReturn(false);
        when(postRepositoryMock.findById("id")).thenReturn(Optional.ofNullable(listOfPostEntities.get(0)));
        when(postRepositoryMock.save(any(Post.class))).thenReturn(listOfPostEntities.get(0));
        when(modifyPostRequestMock.getThumbnailId()).thenReturn(null);
        when(postConverterMock.of(any(ModifyPostRequest.class), any(Post.class))).thenReturn(listOfPostEntities.get(0));
        assertThat(postService.updatePost("id", modifyPostRequestMock)).isEqualTo(listOfPostEntities.get(0));
    }

    @Test
    public void shouldUpdatePostThrowsWhenSlugIsNotUnique() {
        when(modifyPostRequestMock.getSlug()).thenReturn("slug");
        when(postRepositoryMock.existsBySlugAndIdNot("slug", "id")).thenReturn(true);
        assertThatThrownBy(() -> postService.updatePost("id", modifyPostRequestMock)).isInstanceOf(PostSlugIsNotUnique.class);
    }

    @Test
    public void shouldUpdatePostThrowsWhenThumbnailNotFound() {
        when(postRepositoryMock.existsBySlugAndIdNot(any(), anyString())).thenReturn(false);
        when(postRepositoryMock.findById("id")).thenReturn(Optional.ofNullable(listOfPostEntities.get(0)));
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
