package pl.edu.pk.cosmo.habsatbackend.postsservice.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Accessors(chain = true)
@Document("posts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Post {
    @MongoId
    @Field("_id")
    private String id;

    @Setter
    private String thumbnailId;

    @DBRef
    @Setter
    private Media thumbnail;

    @Setter
    private String title;

    @Setter
    @Indexed(unique = true)
    private String slug;

    @Setter
    private String content;

    @Setter
    private String emailOfAuthor;

    @Setter
    private Date publishedAt;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
