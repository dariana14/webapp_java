package test;

import config.MvcConfig;
import config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = { MvcConfig.class, SecurityConfig.class })
@PropertySource("classpath:application.properties")
public class SecurityIntegrationTests {

    @Value("${jwt.signing.key}")
    private String jwtKey;

    private WebApplicationContext wac;

    @Autowired
    public void setWac(WebApplicationContext wac) {
        this.wac = wac;
    }

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .apply(sharedHttpSession())
                .build();
    }

    @Test
    public void apiUrlsNeedAuthentication() throws Exception {
        mvc.perform(get("/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void apiOrdersDoesNotNeedAuthentication() throws Exception {
        mvc.perform(get("/orders"))
                .andExpect(status().isOk());

        mvc.perform(get("/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void adminCanSeeMoreInfo() throws Exception {
        var jwtToken = getJwt("admin", "admin");

        mvc.perform(get("/users")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk());

        mvc.perform(get("/users/user")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk());

        jwtToken = getJwt("user", "user");

        mvc.perform(get("/users")
                        .header("Authorization", jwtToken))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void canAccessWithJwtToken() throws Exception {

        var jwtToken = getJwt("user", "user");

        mvc.perform(get("/info"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/info")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk());
    }

    private String getJwt(String username, String password) throws Exception {
        String json = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        MvcResult mvcResult = mvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus(), is(200));

        return mvcResult.getResponse().getHeader("Authorization");
    }
}
