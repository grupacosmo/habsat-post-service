package pl.edu.pk.cosmo.habsatbackend.postsservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pk.cosmo.habsatbackend.postsservice.converters.MediaResourceConverter;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.models.MediaSort;
import pl.edu.pk.cosmo.habsatbackend.postsservice.request.PagingRequest;
import pl.edu.pk.cosmo.habsatbackend.postsservice.services.MediaService;
import pl.edu.pk.cosmo.habsatbackend.postsservice.utlis.Paging;

import javax.validation.Valid;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;
    private final MediaResourceConverter mediaResourceConverter;

    @GetMapping
    public ResponseEntity<?> findAllMedia(@Valid @ParameterObject PagingRequest pagingRequest) {
        Page<Media> media = mediaService.findAllMedia(Paging.of(pagingRequest, MediaSort.class));
        return new ResponseEntity<>(media.map(mediaResourceConverter::of), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findMediaById(@PathVariable String id) throws MediaNotFoundException {
        Media media = mediaService.findMediaById(id);
        return new ResponseEntity<>(mediaResourceConverter.of(media), HttpStatus.OK);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadMedia(@RequestPart("file") MultipartFile file) {
        Media media = mediaService.uploadMedia(file);
        return new ResponseEntity<>(mediaResourceConverter.of(media), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedia(@PathVariable String id) throws MediaNotFoundException {
        mediaService.deleteMedia(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
