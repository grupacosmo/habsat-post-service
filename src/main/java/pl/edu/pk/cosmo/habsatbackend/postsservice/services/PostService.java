package pl.edu.pk.cosmo.habsatbackend.postsservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pk.cosmo.habsatbackend.postsservice.converters.PostConverter;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.PostNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.PostSlugIsNotUnique;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.MediaRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.PostRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.ModifyPostRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MediaRepository mediaRepository;
    private final PostConverter postConverter;


    public List<Post> findAllPosts() {
        return postRepository.findAllByOrderById();
    }

    public Post findPostById(String id) throws PostNotFoundException {
        return postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    }

    @Transactional
    public Post createPost(ModifyPostRequest modifyPostRequest) throws PostSlugIsNotUnique, MediaNotFoundException {
        throwIfSlugIsNotUnique(modifyPostRequest.getSlug());

        Post post = postConverter.of(modifyPostRequest, "email_of_author@from_authorization.claims");
        if (modifyPostRequest.getThumbnailId() != null) {
            Media thumbnail = mediaRepository.findById(modifyPostRequest.getThumbnailId()).orElseThrow(MediaNotFoundException::new);
            post.setThumbnail(thumbnail);
        }

        return postRepository.save(post);
    }

    private void throwIfSlugIsNotUnique(String slug) throws PostSlugIsNotUnique {
        if (postRepository.existsBySlug(slug)) throw new PostSlugIsNotUnique();
    }

    @Transactional
    public Post updatePost(String id, ModifyPostRequest modifyPostRequest) throws PostSlugIsNotUnique, PostNotFoundException, MediaNotFoundException {
        throwIfSlugIsNotUnique(modifyPostRequest.getSlug(), id);

        Post currentPost = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        Post updatedPost = postConverter.of(modifyPostRequest, currentPost);
        if (modifyPostRequest.getThumbnailId() != null) {
            Media thumbnail = mediaRepository.findById(modifyPostRequest.getThumbnailId()).orElseThrow(MediaNotFoundException::new);
            updatedPost.setThumbnail(thumbnail);
        }

        return postRepository.save(updatedPost);
    }

    private void throwIfSlugIsNotUnique(String slug, String id) throws PostSlugIsNotUnique {
        if (postRepository.existsBySlugAndIdNot(slug, id)) throw new PostSlugIsNotUnique();
    }

    @Transactional
    public void deletePost(String id) {
        postRepository.deleteById(id);
    }
}
