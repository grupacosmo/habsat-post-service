package pl.edu.pk.cosmo.habsatbackend.postsservice.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Accessors(chain = true)
@Document("media")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Media {
    @MongoId
    @Field("_id")
    private String id;

    @Setter
    private String s3Key;

    @Setter
    private String nameOfFile;

    @Setter
    private String typeOfFile;

    @Setter
    private Long sizeOfFile;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
