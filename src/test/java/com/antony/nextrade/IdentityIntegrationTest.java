package com.antony.nextrade;

import com.antony.nextrade.config.scripts.DefaultUserSeed;
import com.antony.nextrade.modules.permissions.model.Permission;
import com.antony.nextrade.modules.users.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.config.import=",
        "spring.datasource.url=${NEXTRADE_TEST_DB_URL:jdbc:h2:mem:nextrade;MODE=PostgreSQL;DB_CLOSE_DELAY=-1}",
        "spring.datasource.username=${NEXTRADE_TEST_DB_USERNAME:sa}",
        "spring.datasource.password=${NEXTRADE_TEST_DB_PASSWORD:}",
        "jwt.key=HWzT05Y1nVyN1xOu0qyXAsKp++nX6vXSiKQjvlYMJYU=",
        "nextrade.seed.admin.enabled=true",
        "nextrade.seed.admin.email=admin@nextrade.test",
        "nextrade.seed.admin.password=admin-test-password"
})
@AutoConfigureMockMvc
class IdentityIntegrationTest {
    private static final String ADMIN_EMAIL = "admin@nextrade.test";
    private static final String ADMIN_PASSWORD = "admin-test-password";
    private static final String USER_PASSWORD = "user-test-password";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DefaultUserSeed defaultUserSeed;

    @Test
    void signsUpWithUserRoleAndStoresBothPasswordsAsBcrypt() throws Exception {
        var response = signUp("Novo Usuário", "NOVO@nextrade.test", "Novo", USER_PASSWORD)
                .andExpect(status().isCreated());
        var token = read(response, "/data/token");

        mvc.perform(get("/v1/profile").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("novo@nextrade.test"))
                .andExpect(jsonPath("$.data.username").value("novo"))
                .andExpect(jsonPath("$.data.roleCode").value("user"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
        mvc.perform(get("/v1/roles").header("Authorization", bearer(token)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        assertPasswordIsHashed("novo@nextrade.test", USER_PASSWORD);
        assertPasswordIsHashed(ADMIN_EMAIL, ADMIN_PASSWORD);

        var adminId = userRepository.findByEmail(ADMIN_EMAIL).orElseThrow().getId();
        defaultUserSeed.run(null);
        assertThat(userRepository.findByEmail(ADMIN_EMAIL).orElseThrow().getId()).isEqualTo(adminId);

        signIn("NOVO@nextrade.test", USER_PASSWORD).andExpect(status().isOk());
        signIn("novo@nextrade.test", "wrong-password").andExpect(status().isUnauthorized());
        signUp("Duplicado", "novo@nextrade.test", "outro", USER_PASSWORD).andExpect(status().isConflict());
        signUp("Duplicado", "outro@nextrade.test", "NOVO", USER_PASSWORD).andExpect(status().isConflict());
    }

    @Test
    void rejectsMissingAndInvalidTokensAndOversizedBcryptInput() throws Exception {
        mvc.perform(get("/api/v1/profile").contextPath("/api"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
        mvc.perform(get("/v1/profile").header("Authorization", bearer("invalid-token")))
                .andExpect(status().isUnauthorized());
        signUp("Usuário", "limite@nextrade.test", "limite", "á".repeat(37))
                .andExpect(status().isBadRequest());
    }

    @Test
    void appliesRoleChangesGrantsAndRevocationsToAnAlreadyIssuedToken() throws Exception {
        var adminToken = bearer(read(signIn(ADMIN_EMAIL, ADMIN_PASSWORD).andExpect(status().isOk()), "/data/token"));
        var userToken = bearer(read(signUp("Operador", "operador@nextrade.test", "operador", USER_PASSWORD)
                .andExpect(status().isCreated()), "/data/token"));
        var userId = read(mvc.perform(get("/v1/profile").header("Authorization", userToken))
                .andExpect(status().isOk()), "/data/id");

        mvc.perform(get("/v1/users").header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[?(@.email == 'operador@nextrade.test')].roleCode").value("user"));
        mvc.perform(post("/v1/roles").header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Operador","code":"operador","level":10}
                                """))
                .andExpect(status().isCreated());

        var roleId = findId("/v1/roles", "operador", adminToken);
        var permissionId = findId("/v1/permissions", "ROLE_READ", adminToken);
        var grantBody = """
                {"roleId":"%s","permissionId":"%s"}
                """.formatted(roleId, permissionId);
        var linkId = read(grant(adminToken, grantBody).andExpect(status().isCreated()), "/data/id");
        grant(adminToken, grantBody).andExpect(status().isConflict());
        mvc.perform(get("/v1/role-permissions").header("Authorization", adminToken)
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[?(@.id == '%s')].permissionCode".formatted(linkId))
                        .value("ROLE_READ"));

        mvc.perform(put("/v1/users/{id}/role", userId).header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"roleId\":\"%s\"}".formatted(roleId)))
                .andExpect(status().isForbidden());
        mvc.perform(put("/v1/users/{id}/role", userId).header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"roleId\":\"%s\"}".formatted(roleId)))
                .andExpect(status().isNoContent());

        mvc.perform(get("/v1/roles").header("Authorization", userToken)).andExpect(status().isOk());
        mvc.perform(get("/v1/profile").header("Authorization", userToken)).andExpect(status().isForbidden());
        mvc.perform(delete("/v1/role-permissions/{id}", linkId).header("Authorization", adminToken))
                .andExpect(status().isNoContent());
        mvc.perform(get("/v1/roles").header("Authorization", userToken)).andExpect(status().isForbidden());

        var restoredId = read(grant(adminToken, grantBody).andExpect(status().isCreated()), "/data/id");
        assertThat(restoredId).isEqualTo(linkId);
        mvc.perform(get("/v1/roles").header("Authorization", userToken)).andExpect(status().isOk());
    }

    @Test
    void seedsEveryDeclaredPermissionAndProtectsAdministratorGrants() throws Exception {
        var adminToken = bearer(read(signIn(ADMIN_EMAIL, ADMIN_PASSWORD).andExpect(status().isOk()), "/data/token"));
        var result = mvc.perform(get("/v1/role-permissions").header("Authorization", adminToken).param("size", "50"))
                .andExpect(status().isOk());
        var items = mapper.readTree(result.andReturn().getResponse().getContentAsByteArray()).at("/data/items");
        var adminGrants = items.valueStream().filter(item -> item.path("roleName").asText().equals("Administrador"))
                .toList();

        assertThat(adminGrants).extracting(item -> item.path("permissionCode").asText())
                .containsExactlyInAnyOrder(java.util.Arrays.stream(Permission.values())
                        .map(Permission::getCode).toArray(String[]::new));

        var grantId = adminGrants.get(0).path("id").asText();
        mvc.perform(delete("/v1/role-permissions/{id}", grantId).header("Authorization", adminToken))
                .andExpect(status().isForbidden());
    }

    private void assertPasswordIsHashed(String email, String password) {
        var hash = userRepository.findByEmail(email).orElseThrow().getPassword();
        assertThat(hash).startsWith("$2").isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, hash)).isTrue();
    }

    private ResultActions signUp(String name, String email, String username, String password) throws Exception {
        return mvc.perform(post("/v1/auth/sign-up").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"%s","email":"%s","username":"%s","password":"%s","roleCode":"admin"}
                        """.formatted(name, email, username, password)));
    }

    private ResultActions signIn(String email, String password) throws Exception {
        return mvc.perform(post("/v1/auth/sign-in").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"%s"}
                        """.formatted(email, password)));
    }

    private ResultActions grant(String bearer, String body) throws Exception {
        return mvc.perform(post("/v1/role-permissions").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private String findId(String path, String code, String bearer) throws Exception {
        var response = mvc.perform(get(path).header("Authorization", bearer).param("size", "50"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
        for (var item : mapper.readTree(response).at("/data/items")) {
            if (code.equals(item.path("code").asText())) {
                return item.path("id").asText();
            }
        }
        throw new AssertionError("Código ausente: " + code);
    }

    private String read(ResultActions result, String pointer) throws Exception {
        return mapper.readTree(result.andReturn().getResponse().getContentAsByteArray()).at(pointer).asText();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
