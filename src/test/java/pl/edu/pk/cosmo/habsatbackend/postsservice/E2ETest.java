package pl.edu.pk.cosmo.habsatbackend.postsservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class E2ETest {
    @LocalServerPort
    private int port;
    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected MongoTemplate mongo;
    @Autowired
    protected ObjectMapper objectMapper;

    @AfterEach
    public void afterEach() {
        mongo.getDb().drop();
    }

    public String api(String path) {
        path = path.startsWith("/") ? path.substring(1) : path;
        return "http://localhost:" + port + "/" + path;
    }
}
