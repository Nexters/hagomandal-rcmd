package com.hagomandal.rcmd;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.core.io.ClassPathResource;

public class TestUtils {

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public static <T> T deserializeTo(String resourcePath, TypeReference<T> typeReference) throws IOException, URISyntaxException {
        return OBJECT_MAPPER.readValue(asString(resourcePath), typeReference);
    }

    public static String asString(String resourcePath) throws IOException, URISyntaxException {
        if (StringUtils.isBlank(resourcePath)) {
            throw new IllegalArgumentException("no resource path given to compare");
        }
        resourcePath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;

        URL resourcePathOnClasspath = (new ClassPathResource(resourcePath)).getURL();
        if (resourcePathOnClasspath == null || !Files.exists(Path.of(resourcePathOnClasspath.toURI()))) {
            throw new IllegalArgumentException("no resource from the given path : " + resourcePath);
        }

        return IOUtils.toString(resourcePathOnClasspath, StandardCharsets.UTF_8);
    }

    public static <T> boolean compareList(List<T> list1, List<T> list2) {
        // NOTE: 중복 고려 안함
        return compareSet(Sets.newHashSet(list1), Sets.newHashSet(list2));
    }

    public static <T> boolean compareSet(Set<T> set1, Set<T> set2) {
        return Sets.difference(set1, set2).isEmpty();
    }
}
