package com.hagomandal.rcmd.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest
public class SynonymGraphServiceImplSpringBootTest {


    @Autowired
    private SynonymGraphServiceImpl target;

    @Test
    void test_resetGraphWithDict() throws IOException, InterruptedException {
        final ClassPathResource dictResource = new ClassPathResource("dict/synonym_test.dict");
        final Path dictPath = Paths.get(dictResource.getURI());
        target.resetGraphWithDict(dictPath);
        Thread.sleep(3000);
    }

    @Test
    void test_resetGraph() throws InterruptedException {
        target.resetGraph();
        Thread.sleep(3000);
    }
}
