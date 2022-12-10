package pl.edu.pk.cosmo.habsatbackend.postsservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Media;

import java.util.List;

@Repository
public interface MediaRepository extends MongoRepository<Media, String> {
    public List<Media> findAllByOrderById();
}
