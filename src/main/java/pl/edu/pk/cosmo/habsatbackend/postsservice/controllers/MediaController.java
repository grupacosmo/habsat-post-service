package pl.edu.pk.cosmo.habsatbackend.postsservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pk.cosmo.habsatbackend.postsservice.converters.MediaResourceConverter;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;
import pl.edu.pk.cosmo.habsatbackend.postsservice.exceptions.MediaNotFoundException;
import pl.edu.pk.cosmo.habsatbackend.postsservice.services.MediaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;
    private final MediaResourceConverter mediaResourceConverter;

    @GetMapping
    public ResponseEntity<?> findAllMedia() {
        List<Media> media = mediaService.findAllMedia();
        return new ResponseEntity<>(media.stream().map(mediaResourceConverter::of).collect(Collectors.toList()), HttpStatus.OK);
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
