package pl.edu.pk.cosmo.habsatbackend.postsservice.converters;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class MediaConverterUnitTest {
    private final MultipartFile multipartFileMock = mock(MultipartFile.class);
    private final MediaConverter mediaConverter = new MediaConverter();

    @AfterEach
    public void afterEach() {
        reset(multipartFileMock);
    }

    @Test
    public void shouldConvertFileAndS3KeyIntoMediaEntity() {
        when(multipartFileMock.getOriginalFilename()).thenReturn("file_name.png");
        when(multipartFileMock.getSize()).thenReturn(1234L);
        when(multipartFileMock.getContentType()).thenReturn("image/png");
        Media media = mediaConverter.of(multipartFileMock, "s3_key");
        assertThat(media).isInstanceOf(Media.class);
        assertThat(media.getId()).isNull();
        assertThat(media.getNameOfFile()).isEqualTo("file_name.png");
        assertThat(media.getSizeOfFile()).isEqualTo(1234L);
        assertThat(media.getTypeOfFile()).isEqualTo("image/png");
        assertThat(media.getS3Key()).isEqualTo("s3_key");
    }
}
