package com.santanna.kronos.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.santanna.kronos.application.dto.EmployeeRequestDto;
import com.santanna.kronos.application.dto.EmployeeResponseDto;
import com.santanna.kronos.application.dto.UpdateRequestDto;
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


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private EmployeeRequestDto employeeRequestDto;
    private UpdateRequestDto updateEmployeeDto;

//    @BeforeEach
//    void setUp() {
//        UUID id = UUID.randomUUID();
//        employee = Employee.builder()
//                .idEmployee(id)
//                .cpf(CPF)
//                .name(NAME)
//                .surname(SURNAME)
//                .email(MAIL)
//                .salary(SALARY)
//                .position(DEVELOPER)
//                .build();
//
//        employeeRequestDto = new EmployeeRequestDto(
//                CPF,
//                NAME,
//                SURNAME,
//                MAIL,
//                SALARY,
//                DEVELOPER
//        );
//        updateEmployeeDto = new UpdateRequestDto(
//                CPF,
//                NAME,
//                SURNAME,
//                MAIL,
//                SALARY,
//                DEVELOPER
//        );
//    }
@BeforeEach
void setUp() {
    // Gerando um CPF único para cada execução de teste.
    // Gera um número aleatório de 11 dígitos (entre 10000000000 e 99999999999)
    long randomCpf = 10000000000L + (long)(Math.random() * 90000000000L);
    String uniqueCpf = Long.toString(randomCpf);

    employeeRequestDto = new EmployeeRequestDto(
            uniqueCpf,
            NAME,
            SURNAME,
            MAIL,
            SALARY,
            DEVELOPER
    );
    // Para o update, alteramos o nome e o email, mantendo o CPF
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
    void testCreateAndGetEmployee() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequestDto)))
                .andExpect(status().isCreated());

        // chamada do metodo get pois o retrono do criar emplooyee e void
        MvcResult result = mockMvc.perform(get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        PaginatedList<EmployeeResponseDto> paginatedList = objectMapper.readValue(
                jsonResponse, new TypeReference<>() {
                });

        assertThat(paginatedList.getContent()).extracting(EmployeeResponseDto::cpf)
                .contains(employeeRequestDto.cpf());
    }

    @Test
    void testUpdateEmployee() throws Exception {
        // 1. Cria o funcionário via POST
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequestDto)))
                .andExpect(status().isCreated());

        // 2. Recupera o funcionário criado usando o GET (listagem)
        MvcResult listResult = mockMvc.perform(get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String listJson = listResult.getResponse().getContentAsString();
        PaginatedList<EmployeeResponseDto> paginatedList = objectMapper.readValue(
                listJson, new TypeReference<>() {
                });

        // Filtra pelo CPF para obter o ID
        EmployeeResponseDto createdEmployee = paginatedList.getContent()
                .stream()
                .filter(e -> e.cpf().equals(employeeRequestDto.cpf()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Employee not found in list"));

        UUID employeeId = createdEmployee.id();

        // 3. Atualiza o funcionário com o updateEmployeeDto
        mockMvc.perform(put(BASE_PATH + "/" + employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEmployeeDto)))
                .andExpect(status().isOk());

        // 4. Realiza um GET para confirmar as atualizações
        MvcResult getResult = mockMvc.perform(get(BASE_PATH + "/" + employeeId))
                .andExpect(status().isOk())
                .andReturn();

        String getJson = getResult.getResponse().getContentAsString();
        EmployeeResponseDto responseDto = objectMapper.readValue(getJson, EmployeeResponseDto.class);

        // Verifica se os dados foram atualizados conforme o DTO de update
        assertThat(responseDto.name()).isEqualTo(updateEmployeeDto.name());
        assertThat(responseDto.email()).isEqualTo(updateEmployeeDto.email());
    }

    @Test
    void testUpdateEmailEmployee() throws Exception {
        // 1. Cria o funcionário via POST
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequestDto)))
                .andExpect(status().isCreated());

        // 2. Recupera o funcionário criado usando o GET (listagem)
        MvcResult listResult = mockMvc.perform(get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String listJson = listResult.getResponse().getContentAsString();
        PaginatedList<EmployeeResponseDto> paginatedList = objectMapper.readValue(
                listJson, new TypeReference<>() {
                });

        // Filtra pelo CPF para obter o ID
        EmployeeResponseDto createdEmployee = paginatedList.getContent()
                .stream()
                .filter(e -> e.cpf().equals(employeeRequestDto.cpf()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Employee not found in list"));

        UUID employeeId = createdEmployee.id();

        // 3. Atualiza o funcionário com o updateEmployeeDto
        mockMvc.perform(put(BASE_PATH + "/email/update/" + employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEmployeeDto)))
                .andExpect(status().isOk());

        // 4. Realiza um GET para confirmar as atualizações
        MvcResult getResult = mockMvc.perform(get(BASE_PATH + "/" + employeeId))
                .andExpect(status().isOk())
                .andReturn();

        String getJson = getResult.getResponse().getContentAsString();
        EmployeeResponseDto responseDto = objectMapper.readValue(getJson, EmployeeResponseDto.class);

        // Verifica se os dados foram atualizados conforme o DTO de update
        assertThat(responseDto.email()).isEqualTo(updateEmployeeDto.email());
    }

    @Test
    void testDeleteEmployee() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequestDto)))
                .andExpect(status().isCreated());

        // 2. Recupera o funcionário criado via GET (listagem) para obter o ID
        MvcResult listResult = mockMvc.perform(get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String listJson = listResult.getResponse().getContentAsString();
        PaginatedList<EmployeeResponseDto> paginatedList = objectMapper.readValue(
                listJson, new TypeReference<>() {});

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
}
