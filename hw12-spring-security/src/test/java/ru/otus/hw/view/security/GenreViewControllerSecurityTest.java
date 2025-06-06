package ru.otus.hw.view.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.view.AuthorViewController;
import ru.otus.hw.view.GenreViewController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = GenreViewController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {"mongock.enabled=false"})
class GenreViewControllerSecurityTest {

    @Autowired
    private MockMvc mvc;

    @WithMockUser(username = "user")
    @Test
    void findAll() throws Exception {
        mvc.perform(get("/genres"))
                .andExpect(view().name("genres"));
    }

    @WithAnonymousUser
    @Test
    void findAll_anonymous() throws Exception {
        mvc.perform(get("/genres"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

}