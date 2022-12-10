package pl.edu.pk.cosmo.habsatbackend.postsservice.converters;

import org.springframework.stereotype.Component;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.ModifyPostRequest;

@Component
public class PostConverter {
    public Post of(ModifyPostRequest modifyPostRequest, String emailOfAuthor) {
        return new Post()
                .setTitle(modifyPostRequest.getTitle())
                .setThumbnailId(modifyPostRequest.getThumbnailId())
                .setSlug(modifyPostRequest.getSlug())
                .setContent(modifyPostRequest.getContent())
                .setPublishedAt(modifyPostRequest.getPublishedAt())
                .setEmailOfAuthor(emailOfAuthor);
    }

    public Post of(ModifyPostRequest modifyPostRequest, Post post) {
        return post
                .setTitle(modifyPostRequest.getTitle())
                .setThumbnailId(modifyPostRequest.getThumbnailId())
                .setSlug(modifyPostRequest.getSlug())
                .setContent(modifyPostRequest.getContent())
                .setPublishedAt(modifyPostRequest.getPublishedAt());
    }
}
