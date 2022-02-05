package com.hagomandal.rcmd.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hagomandal.rcmd.TestUtils;
import com.hagomandal.rcmd.model.input.Mandalart;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KeywordGraphServiceImplSpringBootTest {

    @Autowired
    private KeywordGraphServiceImpl target;

    @Test
    void test_updatePartial() throws IOException, URISyntaxException, InterruptedException {
        Mandalart sample = TestUtils.deserializeTo("sample/sample_mandalart_1.json", new TypeReference<List<Mandalart>>() {}).get(0);
        target.updatePartial(sample.getGoal(), sample.getInfo()).block();
        target.updatePartial(sample.getGoal().getChildren().get(0), sample.getInfo()).block();
        target.updatePartial(sample.getGoal().getChildren().get(1), sample.getInfo()).block();
        target.updatePartial(sample.getGoal().getChildren().get(2), sample.getInfo()).block();
        target.updatePartial(sample.getGoal().getChildren().get(3), sample.getInfo()).block();
        Thread.sleep(5 * 1000L);
    }

    @Test
    void test_update() throws IOException, URISyntaxException, InterruptedException {
        Mandalart sample = TestUtils.deserializeTo("sample/sample_mandalart_1.json", new TypeReference<List<Mandalart>>() {}).get(0);
        target.update(sample).block();
        Thread.sleep(5 * 1000L);
    }
}
