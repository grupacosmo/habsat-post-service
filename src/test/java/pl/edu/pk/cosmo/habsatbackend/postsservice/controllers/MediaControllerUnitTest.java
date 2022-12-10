package pl.edu.pk.cosmo.habsatbackend.postsservice.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pk.cosmo.habsatbackend.postsservice.converters.MediaResourceConverter;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.services.MediaService;
import pl.edu.pk.cosmo.habsatbackend.postsservice.utils.factories.MediaFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MediaControllerUnitTest {
    private final MediaService mediaServiceMock = mock(MediaService.class);
    private final MediaResourceConverter mediaResourceConverterMock = mock(MediaResourceConverter.class);
    private final MultipartFile multipartFileMock = mock(MultipartFile.class);
    private MediaController mediaController;
    private List<Media> listOfMediaEntities;

    @BeforeAll
    public void beforeAll() {
        mediaController = new MediaController(mediaServiceMock, mediaResourceConverterMock);
        listOfMediaEntities = new MediaFactory().createMany(2);
    }

    @AfterEach
    public void afterEach() {
        reset(mediaServiceMock, mediaResourceConverterMock);
    }

    @Test
    public void shouldFindAllMediaAndTransformThemIntoMediaResources() {
        when(mediaServiceMock.findAllMedia()).thenReturn(listOfMediaEntities);
        assertThat(mediaController.findAllMedia().getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(mediaServiceMock, times(1)).findAllMedia();
        verify(mediaResourceConverterMock, times(1)).of(listOfMediaEntities.get(0));
        verify(mediaResourceConverterMock, times(1)).of(listOfMediaEntities.get(1));
    }

    @Test
    public void shouldFindMediaByIdAndTransformItIntoMediaResource() throws MediaNotFoundException {
        when(mediaServiceMock.findMediaById(anyString())).thenReturn(listOfMediaEntities.get(0));
        assertThat(mediaController.findMediaById("id").getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(mediaServiceMock, times(1)).findMediaById("id");
        verify(mediaResourceConverterMock, times(1)).of(listOfMediaEntities.get(0));
    }

    @Test
    public void shouldUploadMediaAndReturnTransformItIntoMediaResource() {
        when(mediaServiceMock.uploadMedia(any(MultipartFile.class))).thenReturn(listOfMediaEntities.get(0));
        assertThat(mediaController.uploadMedia(multipartFileMock).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(mediaServiceMock, times(1)).uploadMedia(any(MultipartFile.class));
        verify(mediaResourceConverterMock, times(1)).of(listOfMediaEntities.get(0));
    }

    @Test
    public void shouldDeleteMediaAndReturnEmptyResponse() throws MediaNotFoundException {
        assertThat(mediaController.deleteMedia("id").getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(mediaServiceMock, times(1)).deleteMedia("id");
    }
}
