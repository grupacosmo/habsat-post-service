package pl.edu.pk.cosmo.habsatbackend.postsservice.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;
import pl.edu.pk.cosmo.habsatbackend.postsservice.resources.PostResource;

@Component
@AllArgsConstructor
public class PostResourceConverter {
    private final MediaResourceConverter mediaResourceConverter;

    public PostResource of(Post post) {
        PostResource resource = new PostResource()
                .setId(post.getId())
                .setThumbnailId(post.getThumbnailId())
                .setTitle(post.getTitle())
                .setSlug(post.getSlug())
                .setContent(post.getContent())
                .setEmailOfAuthor(post.getEmailOfAuthor())
                .setPublishedAt(post.getPublishedAt());

        Media thumbnail = post.getThumbnail();
        if (thumbnail != null) resource.setThumbnail(mediaResourceConverter.of(thumbnail));

        return resource;
    }
}
