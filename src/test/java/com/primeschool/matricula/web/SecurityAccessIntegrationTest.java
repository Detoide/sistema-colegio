package com.primeschool.matricula.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void redirigeAlLoginSiNoHaySesion() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        mvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("docente@colegio.test")
    void docentePuedeVerNotasYAsistenciaPeroNoAdministracion() throws Exception {
        mvc.perform(get("/notas")).andExpect(status().isOk());
        mvc.perform(get("/asistencia")).andExpect(status().isOk());
        mvc.perform(get("/registro-academico")).andExpect(status().isOk());
        mvc.perform(get("/admin/usuarios")).andExpect(status().isForbidden());
        mvc.perform(get("/estudiantes")).andExpect(status().isForbidden());
        mvc.perform(get("/matriculas/nueva")).andExpect(status().isForbidden());
        mvc.perform(post("/matriculas").with(csrf())).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("apoderado@correo.test")
    void apoderadoNoAccedeAPlanillasNiAdministracion() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/matriculas")).andExpect(status().isOk());
        mvc.perform(get("/boletas")).andExpect(status().isOk());
        mvc.perform(get("/notas")).andExpect(status().isForbidden());
        mvc.perform(get("/asistencia")).andExpect(status().isForbidden());
        mvc.perform(get("/registro-academico")).andExpect(status().isForbidden());
        mvc.perform(get("/admin/usuarios")).andExpect(status().isForbidden());
        mvc.perform(get("/estudiantes")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("directivo@colegio.test")
    void directivoAccedeAAdministracionYConsultas() throws Exception {
        mvc.perform(get("/admin/usuarios")).andExpect(status().isOk());
        mvc.perform(get("/admin/periodos")).andExpect(status().isOk());
        mvc.perform(get("/admin/catalogos")).andExpect(status().isOk());
        mvc.perform(get("/matriculas/nueva")).andExpect(status().isOk());
        mvc.perform(get("/notas")).andExpect(status().isOk());
        mvc.perform(get("/asistencia")).andExpect(status().isOk());
        mvc.perform(get("/registro-academico")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("docente@colegio.test")
    void formulariosPostExigenCsrf() throws Exception {
        mvc.perform(post("/notas"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/notas").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
