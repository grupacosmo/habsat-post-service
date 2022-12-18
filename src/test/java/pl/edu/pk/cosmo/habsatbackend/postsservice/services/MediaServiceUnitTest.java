package pl.edu.pk.cosmo.habsatbackend.postsservice.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pk.cosmo.habsatbackend.postsservice.converters.MediaConverter;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.models.MediaSort;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.MediaRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.MediaFactory;
import pl.edu.pk.cosmo.habsatbackend.postsservice.utlis.Paging;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

public class MediaServiceUnitTest {
    private final S3Service s3ServiceMock = mock(S3Service.class);
    private final MediaRepository mediaRepositoryMock = mock(MediaRepository.class);
    private final MediaConverter mediaConverterMock = mock(MediaConverter.class);
    private final MultipartFile multipartFileMock = mock(MultipartFile.class);
    private final Paging<MediaSort> pagingMock = mock(Paging.class);
    private final MediaService mediaService = new MediaService(s3ServiceMock, mediaRepositoryMock, mediaConverterMock);
    private final List<Media> listOfMedia = new MediaFactory().createMany(2);

    private final Page<Media> paginationOfMedia = new PageImpl<Media>(listOfMedia);

    @AfterEach
    public void afterEach() {
        reset(s3ServiceMock, mediaRepositoryMock, mediaConverterMock, multipartFileMock);
    }

    @Test
    public void shouldFindAllMediaReturnsAllMediaEntities() {
        when(mediaRepositoryMock.findAll(any(Pageable.class))).thenReturn(paginationOfMedia);
        when(pagingMock.getPageable()).thenReturn(Pageable.unpaged());
        assertThat(mediaService.findAllMedia(pagingMock)).isEqualTo(paginationOfMedia);
    }

    @Test
    public void shouldFindMediaByIdReturnsMediaEntity() throws MediaNotFoundException {
        when(mediaRepositoryMock.findById("id")).thenReturn(Optional.of(listOfMedia.get(0)));
        assertThat(mediaService.findMediaById("id")).isEqualTo(listOfMedia.get(0));
        verify(mediaRepositoryMock, times(1)).findById("id");
    }

    @Test
    public void shouldFindMediaByIdThrowsWhenMediaNotFound() {
        when(mediaRepositoryMock.findById("id")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> mediaService.findMediaById("id")).isInstanceOf(MediaNotFoundException.class);
    }

    @Test
    public void shouldUploadMediaCreatesMediaEntity() {
        when(s3ServiceMock.uploadFile(multipartFileMock)).thenReturn("s3_key");
        when(mediaConverterMock.of(multipartFileMock, "s3_key")).thenReturn(listOfMedia.get(0));
        when(mediaRepositoryMock.save(any())).thenReturn(listOfMedia.get(0));
        assertThat(mediaService.uploadMedia(multipartFileMock)).isEqualTo(listOfMedia.get(0));
        verify(mediaConverterMock, times(1)).of(multipartFileMock, "s3_key");
        verify(mediaRepositoryMock, times(1)).save(listOfMedia.get(0));
    }

    @Test
    public void shouldUploadMediaUploadsFileToS3() {
        when(s3ServiceMock.uploadFile(multipartFileMock)).thenReturn("s3_key");
        when(mediaConverterMock.of(multipartFileMock, "s3_key")).thenReturn(listOfMedia.get(0));
        when(mediaRepositoryMock.save(any())).thenReturn(listOfMedia.get(0));
        assertThat(mediaService.uploadMedia(multipartFileMock)).isEqualTo(listOfMedia.get(0));
        verify(s3ServiceMock, times(1)).uploadFile(multipartFileMock);
    }

    @Test
    public void shouldDeleteMediaDeletesMediaEntity() throws MediaNotFoundException {
        when(mediaRepositoryMock.findById("id")).thenReturn(Optional.of(listOfMedia.get(0)));
        mediaService.deleteMedia("id");
        verify(mediaRepositoryMock, times(1)).delete(listOfMedia.get(0));
    }

    @Test
    public void shouldDeleteMediaRemovesFileFromS3() throws MediaNotFoundException {
        when(mediaRepositoryMock.findById("id")).thenReturn(Optional.of(listOfMedia.get(0)));
        mediaService.deleteMedia("id");
        verify(s3ServiceMock, times(1)).deleteFile(listOfMedia.get(0).getS3Key());
    }

    @Test
    public void shouldDeleteMediaThrowsWhenMediaNotFound() {
        when(mediaRepositoryMock.findById("id")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> mediaService.deleteMedia("id")).isInstanceOf(MediaNotFoundException.class);
    }
}
