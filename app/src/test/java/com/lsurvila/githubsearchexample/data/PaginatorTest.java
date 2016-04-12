package com.lsurvila.githubsearchexample.data;

import com.lsurvila.githubsearchexample.model.Paginator;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PaginatorTest {

    private Paginator paginator;

    @Before
    public void setUp() throws Exception {
        paginator = new Paginator();
    }

    @Test
    public void shouldBeFirstPage_getNextCalled() throws Exception {
        paginator.getNextPage();

        assertThat(paginator.isFirstPage()).isTrue();
    }

    @Test
    public void shouldNotBeFirstPage_getNextNotCalled() throws Exception {
        assertThat(paginator.isFirstPage()).isFalse();
    }

    @Test
    public void shouldReturn1_getNextPageCalled() throws Exception {
        int page = paginator.getNextPage();

        assertThat(page).isEqualTo(1);
    }

    @Test
    public void shouldReturnInvalidPage_getNextPageCalledTwice_lastPageNotSet() throws Exception {
        paginator.getNextPage();
        int page = paginator.getNextPage();

        assertThat(page).isEqualTo(-1);
    }

    @Test
    public void shouldReturn2_getNextPageCalledTwice_lastPageSet() throws Exception {
        paginator.setLastPage(10);

        paginator.getNextPage();
        int page = paginator.getNextPage();

        assertThat(page).isEqualTo(2);
    }

    @Test
    public void shouldReturnInvalidPage_getNextPageCalledTwice_lastPageIs1() throws Exception {
        paginator.setLastPage(1);

        paginator.getNextPage();
        int page = paginator.getNextPage();

        assertThat(page).isEqualTo(-1);
    }

    @Test
    public void shouldReturn1_afterReset() throws Exception {
        paginator.setLastPage(10);
        paginator.getNextPage();
        int page = paginator.getNextPage();

        assertThat(page).isEqualTo(2);


        paginator.reset();
        page = paginator.getNextPage();

        assertThat(page).isEqualTo(1);
    }
}