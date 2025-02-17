package com.santanna.kronos.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.santanna.kronos.application.dto.company.CompanyRequestDto;
import com.santanna.kronos.application.dto.company.CompanyResponseDto;
import com.santanna.kronos.application.dto.company.UpdateCompanyRequestDto;
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
public class CompanyIntegrationTest {
    public static final String COMPANY_NOT_FOUND_404 = "Empresa não encontrada";
    public static final String NAME_COMPANY = "Minha empresa";
    public static final String BASE_PATH = "/v1/company";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private CompanyRequestDto companyRequestDto;
    private UpdateCompanyRequestDto updateCompanyRequestDto;

    @BeforeEach
    void setUp() {
        long randomCnpj = 1000000000000L + (long) (Math.random() * 9000000000000L);
        String uniqueCnpj = Long.toString(randomCnpj);
        companyRequestDto = new CompanyRequestDto(
                uniqueCnpj,
                NAME_COMPANY
        );
        updateCompanyRequestDto = new UpdateCompanyRequestDto(
                "3219876542310",
                NAME_COMPANY + "updated"
        );
    }

    @Test
    void shouldCreateCompanyAndGetCompany() throws Exception {
        methodPost_isCreated();
        var result = methodGetPaginatedCompanies();
        var paginatedList = getCompanyList(result);

        assertThat(paginatedList.getContent()).extracting(CompanyResponseDto::cnpj)
                .contains(companyRequestDto.cnpj());
    }
    @Test
    void shouldUpdateCompanyAndGetCompany() throws Exception {
        methodPost_isCreated();
        var listResult = methodGetPaginatedCompanies();
        var paginatedList = getCompanyList(listResult);
        var createdCompany = filterByCnpjToGetID(paginatedList);
        var companyId = createdCompany.id();

        mockMvc.perform(put(BASE_PATH + "/" + companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCompanyRequestDto)))
                .andExpect(status().isOk());

        var getResult = methodGetById(companyId);

        var getJson = getResult.getResponse().getContentAsString();
        var responseDto = objectMapper.readValue(getJson, CompanyResponseDto.class);

        assertThat(responseDto.cnpj()).isEqualTo(updateCompanyRequestDto.cnpj());
        assertThat(responseDto.nameCompany()).isEqualTo(updateCompanyRequestDto.nameCompany());
    }
    @Test
    void shouldCompanyGetIdAndGetCompany() throws Exception {
        methodPost_isCreated();
        var listResult = methodGetPaginatedCompanies();
        PaginatedList<CompanyResponseDto> paginatedList = getCompanyList(listResult);

        var createdCompany = filterByCnpjToGetID(paginatedList);
        var companyId = createdCompany.id();

        var getResult = methodGetById(companyId);

        var getJson = getResult.getResponse().getContentAsString();
        var responseDto = objectMapper.readValue(getJson, CompanyResponseDto.class);

        assertThat(responseDto.cnpj()).isEqualTo(companyRequestDto.cnpj());
        assertThat(responseDto.nameCompany()).isEqualTo(companyRequestDto.nameCompany());
    }
    @Test
    void shouldDeleteCompany() throws Exception {
        methodPost_isCreated();
        var listResult = methodGetPaginatedCompanies();
        var paginatedList = getCompanyList(listResult);
        var createdCompany = filterByCnpjToGetID(paginatedList);
        var companyId = createdCompany.id();

        mockMvc.perform(delete(BASE_PATH + "/" + companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCompanyRequestDto)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_PATH + "/" + companyId))
                .andExpect(status().isNotFound());
    }

    private void methodPost_isCreated() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyRequestDto)))
                .andExpect(status().isCreated());
    }

    private MvcResult methodGetById(UUID companyId) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/" + companyId))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MvcResult methodGetPaginatedCompanies() throws Exception {
        return mockMvc.perform(get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();
    }

    private CompanyResponseDto filterByCnpjToGetID(PaginatedList<CompanyResponseDto> paginatedList) {
        return paginatedList.getContent().stream()
                .filter(company -> company.cnpj().equals(companyRequestDto.cnpj()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(COMPANY_NOT_FOUND_404));
    }

    private PaginatedList<CompanyResponseDto> getCompanyList(MvcResult result)
            throws UnsupportedEncodingException, JsonProcessingException {

        var jsonResponse = result.getResponse().getContentAsString();
        return objectMapper.readValue(
                jsonResponse, new TypeReference<>() {
                }
        );
    }


}
