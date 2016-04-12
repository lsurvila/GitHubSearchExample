package com.lsurvila.githubsearchexample.data;

import com.google.gson.Gson;
import com.lsurvila.githubsearchexample.AndroidUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelConverterTest {

    private ModelConverter modelConverter;

    @Mock
    private AndroidUtils androidUtils;

    @Before
    public void setUp() throws Exception {
        modelConverter = new ModelConverter(new Gson(), androidUtils);
    }

    @Test
    public void shouldExtractLastPageUrlFromLinkHeader() throws Exception {
        String linkHeader = "<https://api.github.com/user/repos?page=3&per_page=100>; rel=\"next\",\n" +
                "  <https://api.github.com/user/repos?page=50&per_page=100>; rel=\"last\"";
        String lastPageUrl = modelConverter.getLastPageUrl(linkHeader);
        assertThat(lastPageUrl).isEqualTo("https://api.github.com/user/repos?page=50&per_page=100");
    }

    @Test
    public void shouldLastPageUrlBeNull_noLastRel() throws Exception {
        String linkHeader = "<https://api.github.com/user/repos?page=3&per_page=100>; rel=\"next\",\n" +
                "  <https://api.github.com/user/repos?page=50&per_page=100>; rel=\"first\"";
        String lastPageUrl = modelConverter.getLastPageUrl(linkHeader);
        assertThat(lastPageUrl).isNull();
    }

    @Test
    public void shouldExtractLastPageFromLinkHeader() throws Exception {
        String linkHeader = "<https://api.github.com/user/repos?page=3&per_page=100>; rel=\"next\",\n" +
                "  <https://api.github.com/user/repos?page=50&per_page=100>; rel=\"last\"";
        when(androidUtils.getQueryFromUrl("https://api.github.com/user/repos?page=50&per_page=100", "page")).thenReturn("50");

        int lastPage = modelConverter.getLastPage(linkHeader);

        assertThat(lastPage).isEqualTo(50);
    }

    @Test
    public void shouldExtract1_headerIsNull() throws Exception {
        int lastPage = modelConverter.getLastPage(null);

        assertThat(lastPage).isEqualTo(1);
    }

    @Test
    public void shouldExtract1_headerIsEmpty() throws Exception {
        int lastPage = modelConverter.getLastPage("");

        assertThat(lastPage).isEqualTo(1);
    }
}