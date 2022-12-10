package pl.edu.pk.cosmo.habsatbackend.postsservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    public Boolean existsBySlug(String slug);
    public Boolean existsBySlugAndIdNot(String slug, String id);
    public List<Post> findAllByOrderById();
}
