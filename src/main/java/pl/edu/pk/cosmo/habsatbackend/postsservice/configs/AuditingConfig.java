package pl.edu.pk.cosmo.habsatbackend.postsservice.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class AuditingConfig {}