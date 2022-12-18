package pl.edu.pk.cosmo.habsatbackend.postsservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Boolean existsBySlug(String slug);

    Boolean existsBySlugAndIdNot(String slug, String id);

}
