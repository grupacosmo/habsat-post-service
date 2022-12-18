package pl.edu.pk.cosmo.habsatbackend.postsservice;

import com.github.javafaker.Faker;
import pl.edu.pk.cosmo.habsatbackend.postsservice.entities.Post;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostFactory implements EntityFactory<Post> {
    private final Faker faker = new Faker();

    @Override
    public Post create() {
        String id = faker.internet().uuid();
        String title = faker.book().title();
        String slug = faker.internet().slug();
        String content = faker.lorem().sentence(30);
        String emailOfAuthor = faker.internet().emailAddress();
        Date publishedAt = faker.date().birthday();
        Date createdAt = faker.date().birthday();
        Date updatedAt = faker.date().birthday();

        return new Post(
                id,
                null,
                null,
                title,
                slug,
                content,
                emailOfAuthor,
                publishedAt,
                createdAt,
                updatedAt
        );
    }

    @Override
    public List<Post> createMany(int count) {
        return Stream.generate(this::create).limit(count).collect(Collectors.toList());
    }

}
