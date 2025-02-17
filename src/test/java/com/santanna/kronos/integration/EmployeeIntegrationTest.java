package com.santanna.kronos.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.santanna.kronos.application.dto.employee.EmployeeRequestDto;
import com.santanna.kronos.application.dto.employee.EmployeeResponseDto;
import com.santanna.kronos.application.dto.employee.UpdateRequestDto;
import com.santanna.kronos.application.exception.NotFoundException;
import com.santanna.kronos.domain.common.PaginatedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeIntegrationTest {
    public static final String NAME = "João";
    public static final String SURNAME = "Silva";
    public static final String MAIL = "joao@example.com";
    public static final double SALARY = 2000.0;
    public static final String DEVELOPER = "Developer";
    public static final String BASE_PATH = "/v1/employee";
    public static final String EMPLOYEE_NOT_FOUND_404 = "Colaborador não encontrado";
    public static final String EMAIL_UPDATE = "/email/update/";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private EmployeeRequestDto employeeRequestDto;
    private UpdateRequestDto updateEmployeeDto;

    @BeforeEach
    void setUp() {
        long randomCpf = 10000000000L + (long) (Math.random() * 90000000000L);
        String uniqueCpf = Long.toString(randomCpf);

        employeeRequestDto = new EmployeeRequestDto(
                uniqueCpf,
                NAME,
                SURNAME,
                MAIL,
                SALARY,
                DEVELOPER
        );
        updateEmployeeDto = new UpdateRequestDto(
                uniqueCpf,
                NAME + " Atualizado",
                SURNAME,
                "novo_" + MAIL,
                SALARY,
                DEVELOPER
        );
    }

    @Test
    void shouldCreateAndGetEmployee() throws Exception {
        methodPost_isCreated();
        // chamada do metodo get pois o retrono do criar emplooyee e void
        var result = methodGetPaginatedEmployee();
        var paginatedList = getEmployeeResponseDtoPaginatedList(result);
        assertThat(paginatedList.getContent()).extracting(EmployeeResponseDto::cpf)
                .contains(employeeRequestDto.cpf());
    }
    @Test
    void shouldUpdateEmployeeAndGetEmployee() throws Exception {
        // 1. Cria o funcionário via POST
        methodPost_isCreated();
        // 2. Recupera o funcionário criado usando o GET (listagem)
        var listResult = methodGetPaginatedEmployee();
        var paginatedList = getEmployeeResponseDtoPaginatedList(listResult);

        // Filtra pelo CPF para obter o ID
        var createdEmployee = filterByCpfToGetId(paginatedList);
        var employeeId = createdEmployee.id();

        // 3. Atualiza o funcionário com o updateEmployeeDto
        mockMvc.perform(put(BASE_PATH + "/" + employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEmployeeDto)))
                .andExpect(status().isOk());

        // 4. Realiza um GET para confirmar as atualizações
        MvcResult getResult = methodGetId(employeeId);

        String getJson = getResult.getResponse().getContentAsString();
        EmployeeResponseDto responseDto = objectMapper.readValue(getJson, EmployeeResponseDto.class);

        // Verifica se os dados foram atualizados conforme o DTO de update
        assertThat(responseDto.name()).isEqualTo(updateEmployeeDto.name());
        assertThat(responseDto.email()).isEqualTo(updateEmployeeDto.email());
    }
    @Test
    void shouldUpdateEmailEmployeeAndGetEmployee() throws Exception {
        // 1. Cria o funcionário via POST
        methodPost_isCreated();

        // 2. Recupera o funcionário criado usando o GET (listagem)
        MvcResult listResult = methodGetPaginatedEmployee();

        PaginatedList<EmployeeResponseDto> paginatedList = getEmployeeResponseDtoPaginatedList(listResult);
        // Filtra pelo CPF para obter o ID
        var createdEmployee = filterByCpfToGetId(paginatedList);

        UUID employeeId = createdEmployee.id();

        // 3. Atualiza o funcionário com o updateEmployeeDto
        mockMvc.perform(put(BASE_PATH + EMAIL_UPDATE + employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEmployeeDto)))
                .andExpect(status().isOk());

        // 4. Realiza um GET para confirmar as atualizações
        var getResult = methodGetId(employeeId);

        String getJson = getResult.getResponse().getContentAsString();
        EmployeeResponseDto responseDto = objectMapper.readValue(getJson, EmployeeResponseDto.class);

        // Verifica se os dados foram atualizados conforme o DTO de update
        assertThat(responseDto.email()).isEqualTo(updateEmployeeDto.email());
    }
    @Test
    void shouldDeleteEmployee() throws Exception {
        methodPost_isCreated();

        // 2. Recupera o funcionário criado via GET (listagem) para obter o ID
        MvcResult listResult = methodGetPaginatedEmployee();

        PaginatedList<EmployeeResponseDto> paginatedList = getEmployeeResponseDtoPaginatedList(listResult);

        // Filtra pelo CPF para obter o ID do funcionário criado
        EmployeeResponseDto createdEmployee = paginatedList.getContent()
                .stream()
                .filter(e -> e.cpf().equals(employeeRequestDto.cpf()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_NOT_FOUND_404));

        UUID employeeId = createdEmployee.id();

        // 3. Executa o DELETE para remover o funcionário
        mockMvc.perform(delete(BASE_PATH + "/" + employeeId))
                .andExpect(status().isNoContent());

        // 4. Tenta recuperar o funcionário deletado; espera-se um 404 (Not Found)
        mockMvc.perform(get(BASE_PATH + "/" + employeeId))
                .andExpect(status().isNotFound());
    }

    private void methodPost_isCreated() throws Exception {

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequestDto)))
                .andExpect(status().isCreated());
    }
    private MvcResult methodGetId(UUID employeeId) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/" + employeeId))
                .andExpect(status().isOk())
                .andReturn();
    }
    private MvcResult methodGetPaginatedEmployee() throws Exception {
        return mockMvc.perform(get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();
    }
    private EmployeeResponseDto filterByCpfToGetId(PaginatedList<EmployeeResponseDto> paginatedList) {
        return paginatedList.getContent()
                .stream()
                .filter(e -> e.cpf().equals(employeeRequestDto.cpf()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Employee not found in list"));
    }
    private PaginatedList<EmployeeResponseDto> getEmployeeResponseDtoPaginatedList(MvcResult result)
            throws UnsupportedEncodingException, JsonProcessingException {

        String jsonResponse = result.getResponse().getContentAsString();
        return objectMapper.readValue(
                jsonResponse, new TypeReference<>() {
                });
    }

}
