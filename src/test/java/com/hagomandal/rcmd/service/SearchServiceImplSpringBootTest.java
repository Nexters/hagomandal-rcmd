package com.hagomandal.rcmd.service;

import co.elastic.clients.elasticsearch.core.BulkResponse;
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
public class SearchServiceImplSpringBootTest {

    @Autowired
    private SearchServiceImpl target;

    @Test
    void test_index() throws IOException, URISyntaxException {
        Mandalart sample = TestUtils.deserializeTo("sample/sample_mandalart_1.json", new TypeReference<List<Mandalart>>() {}).get(0);
        BulkResponse bulkResponse = target.index(sample).block();
        System.out.println(bulkResponse);
    }
}
