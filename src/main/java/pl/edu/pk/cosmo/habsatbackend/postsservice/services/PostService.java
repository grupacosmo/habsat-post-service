package pl.edu.pk.cosmo.habsatbackend.postsservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pk.cosmo.habsatbackend.postsservice.converters.PostConverter;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.PostNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.PostSlugIsNotUniqueException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.models.PostSort;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.MediaRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.PostRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.ModifyPostRequest;
import pl.edu.pk.cosmo.habsatbackend.postsservice.utlis.Paging;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MediaRepository mediaRepository;
    private final PostConverter postConverter;

    public Page<Post> findAllPosts(Paging<PostSort> paging) {
        return postRepository.findAll(paging.getPageable());
    }

    public Post findPostById(String id) throws PostNotFoundException {
        return postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    }

    @Transactional
    public Post createPost(ModifyPostRequest modifyPostRequest) throws PostSlugIsNotUniqueException, MediaNotFoundException {
        throwIfSlugIsNotUnique(modifyPostRequest.getSlug());

        Post post = postConverter.of(modifyPostRequest, "email_of_author@from_authorization.claims");
        if (modifyPostRequest.getThumbnailId() != null) {
            Media thumbnail = mediaRepository.findById(modifyPostRequest.getThumbnailId()).orElseThrow(MediaNotFoundException::new);
            post.setThumbnail(thumbnail);
        }

        return postRepository.save(post);
    }

    private void throwIfSlugIsNotUnique(String slug) throws PostSlugIsNotUniqueException {
        if (postRepository.existsBySlug(slug)) throw new PostSlugIsNotUniqueException();
    }

    @Transactional
    public Post updatePost(String id, ModifyPostRequest modifyPostRequest) throws PostSlugIsNotUniqueException, PostNotFoundException, MediaNotFoundException {
        throwIfSlugIsNotUnique(modifyPostRequest.getSlug(), id);

        Post currentPost = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        Post updatedPost = postConverter.of(modifyPostRequest, currentPost);
        if (modifyPostRequest.getThumbnailId() != null) {
            Media thumbnail = mediaRepository.findById(modifyPostRequest.getThumbnailId()).orElseThrow(MediaNotFoundException::new);
            updatedPost.setThumbnail(thumbnail);
        }

        return postRepository.save(updatedPost);
    }

    private void throwIfSlugIsNotUnique(String slug, String id) throws PostSlugIsNotUniqueException {
        if (postRepository.existsBySlugAndIdNot(slug, id)) throw new PostSlugIsNotUniqueException();
    }

    @Transactional
    public void deletePost(String id) {
        postRepository.deleteById(id);
    }
}
