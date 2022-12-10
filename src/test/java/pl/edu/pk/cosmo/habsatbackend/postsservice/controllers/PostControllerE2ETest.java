package pl.edu.pk.cosmo.habsatbackend.postsservice.controllers;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import pl.edu.pk.cosmo.habsatbackend.postsservice.E2ETest;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.PostRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.resources.PostResource;
import pl.edu.pk.cosmo.habsatbackend.postsservice.utils.factories.PostFactory;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostControllerE2ETest extends E2ETest {
    @Autowired
    private PostRepository postRepository;

    @Test
    public void shouldFindAllPostsReturnsAllPosts() throws Exception {
        List<Post> listOfPostEntities = new PostFactory().createMany(2);
        postRepository.saveAll(listOfPostEntities);

        mvc.perform(get(api("/posts")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.[0]").exists())
                .andExpect(jsonPath("$.[1]").exists())
                .andExpect(jsonPath("$.[2]").doesNotExist());
    }

    @Test
    public void shouldFindPostByIdReturnPost() throws Exception {
        Post post = postRepository.save(new PostFactory().create());

        mvc.perform(get(api("/posts/" + post.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.thumbnailId").value(post.getThumbnailId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.slug").value(post.getSlug()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.emailOfAuthor").value(post.getEmailOfAuthor()));
    }

    @Test
    public void shouldCreatePostCreatesAndReturnsPost() throws Exception {
        JSONObject payload = new JSONObject(getModifyPostRequestPayload());
        MvcResult result = mvc.perform(post(api("/posts")).contentType(MediaType.APPLICATION_JSON).content(getModifyPostRequestPayload()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.title").value(payload.get("title")))
                .andExpect(jsonPath("$.slug").value(payload.get("slug")))
                .andExpect(jsonPath("$.content").value(payload.get("content")))
                .andExpect(jsonPath("$.publishedAt").value(payload.get("publishedAt")))
                .andExpect(jsonPath("$.emailOfAuthor").value("email_of_author@from_authorization.claims"))
                .andReturn();

        assertThat(mongo.count(new Query(), Post.class)).isEqualTo(1);

        PostResource resource = objectMapper.readValue(result.getResponse().getContentAsString(), PostResource.class);
        Post post = mongo.findById(resource.getId(), Post.class);
        assertThat(post.getId()).isNotNull();
        assertThat(post.getThumbnailId()).isNull();
        assertThat(post.getThumbnail()).isNull();
        assertThat(post.getTitle()).isEqualTo(payload.getString("title"));
        assertThat(post.getSlug()).isEqualTo(payload.getString("slug"));
        assertThat(post.getContent()).isEqualTo(payload.getString("content"));
        assertThat(post.getEmailOfAuthor()).isEqualTo("email_of_author@from_authorization.claims");
    }

    @Test
    public void shouldUpdatePostUpdatesAndReturnsPost() throws Exception {
        Post post = postRepository.save(new PostFactory().create());

        JSONObject payload = new JSONObject(getModifyPostRequestPayload());
        MvcResult result = mvc.perform(put(api("/posts/" + post.getId())).contentType(MediaType.APPLICATION_JSON).content(getModifyPostRequestPayload()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value(payload.get("title")))
                .andExpect(jsonPath("$.slug").value(payload.get("slug")))
                .andExpect(jsonPath("$.content").value(payload.get("content")))
                .andExpect(jsonPath("$.publishedAt").value(payload.get("publishedAt")))
                .andExpect(jsonPath("$.emailOfAuthor").value(post.getEmailOfAuthor()))
                .andReturn();

        assertThat(mongo.count(new Query(), Post.class)).isEqualTo(1);

        PostResource resource = objectMapper.readValue(result.getResponse().getContentAsString(), PostResource.class);
        Post updatedPost = mongo.findById(resource.getId(), Post.class);
        assertThat(updatedPost.getId()).isEqualTo(post.getId());
        assertThat(updatedPost.getThumbnailId()).isNull();
        assertThat(updatedPost.getThumbnail()).isNull();
        assertThat(updatedPost.getTitle()).isEqualTo(payload.getString("title"));
        assertThat(updatedPost.getSlug()).isEqualTo(payload.getString("slug"));
        assertThat(updatedPost.getContent()).isEqualTo(payload.getString("content"));
        assertThat(updatedPost.getEmailOfAuthor()).isEqualTo(post.getEmailOfAuthor());
    }

    private String getModifyPostRequestPayload() {
        return "{" +
                "\"thumbnailId\":null," +
                "\"title\":\"Why should you test your code?\"," +
                "\"slug\":\"why-should-you-test-your-code\"," +
                "\"content\":\"<h1>Intro</h1><p>Lorem ipsum dolor sit amend...</p1>\"," +
                "\"publishedAt\":\"2022-11-05T19:48:23.000+00:00\"" +
                "}";
    }

    @Test
    public void shouldDeletePostDeletesPost() throws Exception {
        Post post = postRepository.save(new PostFactory().create());
        mvc.perform(delete(api("/posts/" + post.getId())))
                .andExpect(status().isNoContent());

        // assertThat(table("posts")).hasNumberOfRows(0);
    }
}
