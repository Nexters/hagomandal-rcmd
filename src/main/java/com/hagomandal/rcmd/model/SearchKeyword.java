package com.hagomandal.rcmd.model;

import com.hagomandal.rcmd.model.graph.keyword.KeywordEntity;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class SearchKeyword {

    private final String keyword;
    private final float weight;

    @Override
    public boolean equals(Object o) {
        if (o instanceof SearchKeyword) {
            SearchKeyword target = (SearchKeyword) o;
            return keyword.equals(target.getKeyword());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword);
    }

    @Override
    public String toString() {
        return String.format("SearchKeyword (%s, %f)", keyword, weight);
    }
}
