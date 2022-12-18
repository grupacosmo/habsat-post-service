package pl.edu.pk.cosmo.habsatbackend.postsservice.converters;

import org.junit.jupiter.api.Test;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;
import pl.edu.pk.cosmo.habsatbackend.postsservice.resources.PostResource;
import pl.edu.pk.cosmo.habsatbackend.postsservice.PostFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class PostResourceConverterUnitTest {
    private final MediaResourceConverter mediaResourceConverterMock = mock(MediaResourceConverter.class);
    private final PostResourceConverter postResourceConverter = new PostResourceConverter(mediaResourceConverterMock);

    @Test
    public void shouldConvertPostEntityIntoPostResource() {
        Post entity = new PostFactory().create();
        PostResource resource = postResourceConverter.of(entity);
        assertThat(resource).isInstanceOf(PostResource.class);
        assertThat(resource.getId()).isEqualTo(entity.getId());
        assertThat(resource.getThumbnailId()).isEqualTo(entity.getThumbnailId());
        assertThat(resource.getTitle()).isEqualTo(entity.getTitle());
        assertThat(resource.getSlug()).isEqualTo(entity.getSlug());
        assertThat(resource.getContent()).isEqualTo(entity.getContent());
        assertThat(resource.getEmailOfAuthor()).isEqualTo(entity.getEmailOfAuthor());
        assertThat(resource.getPublishedAt()).isEqualTo(entity.getPublishedAt());
    }
}
