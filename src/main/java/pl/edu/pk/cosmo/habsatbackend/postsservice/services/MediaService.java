package pl.edu.pk.cosmo.habsatbackend.postsservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pk.cosmo.habsatbackend.postsservice.converters.MediaConverter;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.models.MediaSort;
import pl.edu.pk.cosmo.habsatbackend.postsservice.repositories.MediaRepository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.utlis.Paging;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final S3Service s3Service;
    private final MediaRepository mediaRepository;
    private final MediaConverter mediaConverter;

    public Page<Media> findAllMedia(Paging<MediaSort> paging) {
        return mediaRepository.findAll(paging.getPageable());
    }

    public Media findMediaById(String id) throws MediaNotFoundException {
        return mediaRepository.findById(id).orElseThrow(MediaNotFoundException::new);
    }

    @Transactional
    public Media uploadMedia(MultipartFile file) {
        String s3Key = s3Service.uploadFile(file);
        Media media = mediaConverter.of(file, s3Key);
        return mediaRepository.save(media);
    }

    @Transactional
    public void deleteMedia(String id) throws MediaNotFoundException {
        Media media = mediaRepository.findById(id).orElseThrow(MediaNotFoundException::new);
        s3Service.deleteFile(media.getS3Key());
        mediaRepository.delete(media);
    }

}
