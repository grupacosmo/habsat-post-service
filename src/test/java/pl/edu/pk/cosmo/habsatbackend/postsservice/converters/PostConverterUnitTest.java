package pl.edu.pk.cosmo.habsatbackend.postsservice.converters;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.ModifyPostRequest;
import pl.edu.pk.cosmo.habsatbackend.postsservice.PostFactory;

import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostConverterUnitTest {
    private final PostConverter postConverter = new PostConverter();
    private ModifyPostRequest modifyPostRequest;

    @BeforeAll
    public void beforeAll() {
        modifyPostRequest = new ModifyPostRequest()
                .setThumbnailId("id")
                .setTitle("title")
                .setContent("content")
                .setSlug("slug")
                .setPublishedAt(Timestamp.from(Instant.now()));
    }

    @Test
    public void shouldConvertModifyPostRequestAndEmailIntoPostEntity() {
        Post post = postConverter.of(modifyPostRequest, "email");
        assertThat(post).isInstanceOf(Post.class);
        assertThat(post.getId()).isNull();
        assertThat(post.getThumbnailId()).isEqualTo(modifyPostRequest.getThumbnailId());
        assertThat(post.getTitle()).isEqualTo(modifyPostRequest.getTitle());
        assertThat(post.getContent()).isEqualTo(modifyPostRequest.getContent());
        assertThat(post.getSlug()).isEqualTo(modifyPostRequest.getSlug());
        assertThat(post.getPublishedAt()).isEqualTo(modifyPostRequest.getPublishedAt());
        assertThat(post.getEmailOfAuthor()).isEqualTo("email");
    }

    @Test
    public void shouldUpdatePostEntityWithModifyPostRequest() {
        Post originalPost = new PostFactory().create();
        Post currentPost = postConverter.of(modifyPostRequest, originalPost);
        assertThat(currentPost.getId()).isEqualTo(originalPost.getId());
        assertThat(currentPost.getThumbnailId()).isEqualTo(modifyPostRequest.getThumbnailId());
        assertThat(currentPost.getTitle()).isEqualTo(modifyPostRequest.getTitle());
        assertThat(currentPost.getContent()).isEqualTo(modifyPostRequest.getContent());
        assertThat(currentPost.getSlug()).isEqualTo(modifyPostRequest.getSlug());
        assertThat(currentPost.getPublishedAt()).isEqualTo(modifyPostRequest.getPublishedAt());
        assertThat(currentPost.getEmailOfAuthor()).isEqualTo(originalPost.getEmailOfAuthor());
    }
}
