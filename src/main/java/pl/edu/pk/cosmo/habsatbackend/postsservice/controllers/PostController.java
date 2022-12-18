package pl.edu.pk.cosmo.habsatbackend.postsservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.cosmo.habsatbackend.postsservice.converters.PostResourceConverter;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.PostNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.PostSlugIsNotUniqueException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.models.PostSort;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.ModifyPostRequest;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.PagingRequest;
import pl.edu.pk.cosmo.habsatbackend.postsservice.services.PostService;
import pl.edu.pk.cosmo.habsatbackend.postsservice.utlis.Paging;

import javax.validation.Valid;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostResourceConverter postResourceConverter;

    @GetMapping
    public ResponseEntity<?> findAllPosts(@Valid PagingRequest pagingRequest) {
        Page<Post> posts = postService.findAllPosts(Paging.of(pagingRequest, PostSort.class));
        return new ResponseEntity<>(posts.map(postResourceConverter::of), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findPostById(@PathVariable String id) throws PostNotFoundException {
        Post post = postService.findPostById(id);
        return new ResponseEntity<>(postResourceConverter.of(post), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody ModifyPostRequest modifyPostRequest) throws MediaNotFoundException, PostSlugIsNotUniqueException {
        Post post = postService.createPost(modifyPostRequest);
        return new ResponseEntity<>(postResourceConverter.of(post), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable String id, @Valid @RequestBody ModifyPostRequest modifyPostRequest) throws PostSlugIsNotUniqueException, PostNotFoundException, MediaNotFoundException {
        Post post = postService.updatePost(id, modifyPostRequest);
        return new ResponseEntity<>(postResourceConverter.of(post), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id) {
        postService.deletePost(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
