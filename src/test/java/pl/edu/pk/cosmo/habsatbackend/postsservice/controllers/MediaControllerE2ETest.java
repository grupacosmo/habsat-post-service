package pl.edu.pk.cosmo.habsatbackend.postsservice.controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import pl.edu.pk.cosmo.habsatbackend.postsservice.E2ETest;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.MediaRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.resources.MediaResource;
import pl.edu.pk.cosmo.habsatbackend.postsservice.MediaFactory;

import java.util.List;

import static org.assertj.db.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class MediaControllerE2ETest extends E2ETest {
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private AmazonS3 client;
    @Value("${cloud.aws.s3-bucket}")
    private String nameOfBucket;

    @Test
    public void shouldFindAllMediaReturnsAllMedia() throws Exception {
        List<Media> listOfMediaEntities = new MediaFactory().createMany(2);
        mediaRepository.saveAll(listOfMediaEntities);

        mvc.perform(get(api("/media")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.[0]").exists())
                .andExpect(jsonPath("$.[1]").exists())
                .andExpect(jsonPath("$.[2]").doesNotExist());
    }

    @Test
    public void shouldFindMediaByIdReturnMedia() throws Exception {
        Media mediaEntity = mediaRepository.save(new MediaFactory().create());

        mvc.perform(get(api("/media/" + mediaEntity.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mediaEntity.getId()))
                .andExpect(jsonPath("$.url").value(client.getUrl(nameOfBucket, mediaEntity.getS3Key()).toString()))
                .andExpect(jsonPath("$.nameOfFile").value(mediaEntity.getNameOfFile()))
                .andExpect(jsonPath("$.typeOfFile").value(mediaEntity.getTypeOfFile()))
                .andExpect(jsonPath("$.sizeOfFile").value(mediaEntity.getSizeOfFile()));
    }

    @Test
    public void shouldUploadMediaHandlesFileAndReturnMedia() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

        MvcResult result = mvc.perform(multipart(api("/media")).file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.url").isString())
                .andExpect(jsonPath("$.nameOfFile").value(file.getOriginalFilename()))
                .andExpect(jsonPath("$.typeOfFile").value(file.getContentType()))
                .andExpect(jsonPath("$.sizeOfFile").value(file.getSize()))
                .andReturn();

        assertThat(mongo.count(new Query(), Media.class)).isEqualTo(1);

        MediaResource resource = objectMapper.readValue(result.getResponse().getContentAsString(), MediaResource.class);
        Media media = mongo.findById(resource.getId(), Media.class);
        assertThat(media.getId()).isNotNull().isInstanceOf(String.class);
        assertThat(media.getS3Key()).isNotNull().isInstanceOf(String.class);
        assertThat(media.getNameOfFile()).isEqualTo(file.getOriginalFilename());
        assertThat(media.getTypeOfFile()).isEqualTo(file.getContentType());
        assertThat(media.getSizeOfFile()).isEqualTo(file.getSize());

        assertThat(client.doesObjectExist(nameOfBucket, media.getS3Key())).isTrue();
    }

    @Test
    public void shouldDeleteMediaDeletesMedia() throws Exception {
        Media mediaEntity = mediaRepository.save(new MediaFactory().create());
        MockMultipartFile file = new MockMultipartFile("file", mediaEntity.getNameOfFile(), mediaEntity.getTypeOfFile(), "Hello, World!".getBytes());
        client.putObject(nameOfBucket, mediaEntity.getS3Key(), file.getInputStream(), new ObjectMetadata());

        mvc.perform(delete(api("/media/" + mediaEntity.getId())))
                .andExpect(status().isNoContent());

        assertThat(mongo.count(new Query(), Media.class)).isEqualTo(0);
        assertThat(client.doesObjectExist(nameOfBucket, mediaEntity.getS3Key())).isFalse();
    }
}
