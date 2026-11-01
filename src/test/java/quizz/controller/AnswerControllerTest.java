package quizz.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import quizz.config.JwtUtil;
import quizz.config.SecurityConfig;
import quizz.dto.answer.AnswerDto;
import quizz.dto.answer.AnswerDtoWithoutCorrectFieldDto;
import quizz.dto.answer.AnswerRequestDto;
import quizz.dto.answer.UpdateAnswerDto;
import quizz.exception.EntityNotFoundException;
import quizz.service.CustomUserDetailsService;
import quizz.service.interfaces.AnswerService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AnswerController.class)
@Import(SecurityConfig.class)
public class AnswerControllerTest {
    private static final Long ID = 1L;
    private static final String USERNAME = "test@test.com";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnswerService answerService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void createAnswer_ValidRequestDto_ReturnsCreated() throws Exception {
        //given
        AnswerRequestDto answerRequestDto = createRequest();
        AnswerDto expected = createAnswerDto();
        when(answerService.createAnswer(answerRequestDto)).thenReturn(expected);

        //when & then
        MvcResult result = mockMvc.perform(post("/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String json = result.getResponse().getContentAsString();

        AnswerDto actual = objectMapper.readValue(json, AnswerDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "InvalidRole")
    void createAnswer_InvalidRole_ReturnsForbidden() throws Exception {
        //given
        AnswerRequestDto answerRequestDto = createRequest();

        //when & then
        mockMvc.perform(post("/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void createAnswer_InvalidRequest_ReturnsBadRequest() throws Exception {
        //given
        AnswerRequestDto answerRequestDto = new AnswerRequestDto();

        //when & then
        mockMvc.perform(post("/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void getAnswer_ValidId_ReturnsOk() throws Exception {
        //given
        AnswerDto expected = createAnswerDto();

        when(answerService.getAnswer(ID)).thenReturn(expected);

        //when & then
        MvcResult result = mockMvc.perform(get("/answers/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        AnswerDto actual = objectMapper.readValue(json, AnswerDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void getAnswer_InvalidId_ReturnsNotFound() throws Exception {
        //given
        when(answerService.getAnswer(ID)).thenThrow(EntityNotFoundException.class);

        //when & then
        mockMvc.perform(get("/answers/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "InvalidRole")
    void getAnswer_InvalidRole_ReturnsForbidden() throws Exception {
        //when & then
        mockMvc.perform(get("/answers/{id}", ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void getAnswersByQuestionId_ValidId_ReturnsOk() throws Exception {
        //given
        AnswerDto answerDto = createAnswerDto();
        List<AnswerDto> expected = List.of(answerDto);
        when(answerService.getAnswersByQuestionId(ID)).thenReturn(expected);

        //when & then
        mockMvc.perform(get("/answers/question/{questionId}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionId")
                        .value(answerDto.getQuestionId()));
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void getAnswersByQuestionId_EmptyList_ReturnsOk() throws Exception {
        //given
        when(answerService.getAnswersByQuestionId(ID)).thenReturn(List.of());

        //when & then
        mockMvc.perform(get("/answers/question/{questionId}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "InvalidUser")
    void getAnswersByQuestionId_InvalidRole_ReturnsForbidden() throws Exception {
        //when & then
        mockMvc.perform(get("/answers/question/{questionId}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void getAnswersByQuestionIdWithoutCorrectField_ValidId_ReturnsOk() throws Exception {
        //given
        AnswerDtoWithoutCorrectFieldDto answer = createAnswerDtoWithout();
        List<AnswerDtoWithoutCorrectFieldDto> expected = List.of(answer);

        when(answerService.getAnswersByQuestionIdWithoutCorrectField(ID))
                .thenReturn(expected);

        //when & then
        mockMvc.perform(get("/answers/question/{questionId}/quiz", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].questionId").value(answer.getQuestionId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void getAnswersByQuestionIdWithoutCorrectField_EmptyList_ReturnsOk() throws Exception {
        //given
        when(answerService.getAnswersByQuestionIdWithoutCorrectField(ID)).thenReturn(List.of());

        //when & then
        mockMvc.perform(get("/answers/question/{questionId}/quiz", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "InvalidRole")
    void getAnswersByQuestionIdWithoutCorrectField_InvalidRole_ReturnsForbidden()
            throws Exception {
        //when & then
        mockMvc.perform(get("/answers/question/{questionId}/quiz", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void updateAnswer_ValidDto_ReturnsOk() throws Exception {
        //given
        UpdateAnswerDto updateAnswerDto = createUpdateAnswerDto();
        AnswerDto expected = createAnswerDto();

        when(answerService.updateAnswer(updateAnswerDto)).thenReturn(expected);

        //when & then
        MvcResult result = mockMvc.perform(put("/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAnswerDto)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        AnswerDto actual = objectMapper.readValue(json, AnswerDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void updateAnswer_EmptyDto_ReturnsOk() throws Exception {
        //given
        UpdateAnswerDto updateAnswerDto = new UpdateAnswerDto();

        //when & then
        mockMvc.perform(put("/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAnswerDto)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "GUEST")
    void updateAnswer_InvalidRole_ReturnsForbidden() throws Exception {
        //given
        UpdateAnswerDto updateAnswerDto = createUpdateAnswerDto();

        //when & then
        mockMvc.perform(put("/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAnswerDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void deleteAnswer_ValidId_ReturnsNoContent() throws Exception {
        //when & then
        mockMvc.perform(delete("/answers/{id}", ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "GUEST")
    void deleteAnswer_InvalidRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/answers/{id}", ID))
                .andExpect(status().isForbidden());
    }

    private UpdateAnswerDto createUpdateAnswerDto() {
        UpdateAnswerDto updateAnswerDto = new UpdateAnswerDto();
        updateAnswerDto.setCorrect(true);
        updateAnswerDto.setText("Basic text");
        updateAnswerDto.setId(ID);
        return updateAnswerDto;
    }

    private AnswerDtoWithoutCorrectFieldDto createAnswerDtoWithout() {
        AnswerDtoWithoutCorrectFieldDto answerDtoWithoutCorrectFieldDto
                = new AnswerDtoWithoutCorrectFieldDto();
        answerDtoWithoutCorrectFieldDto.setQuestionId(ID);
        answerDtoWithoutCorrectFieldDto.setPoints(10);
        answerDtoWithoutCorrectFieldDto.setText("Basic text");
        answerDtoWithoutCorrectFieldDto.setId(ID);
        return answerDtoWithoutCorrectFieldDto;
    }

    private AnswerRequestDto createRequest() {
        AnswerRequestDto answerRequestDto = new AnswerRequestDto();
        answerRequestDto.setCorrect(true);
        answerRequestDto.setText("Basic text");
        answerRequestDto.setQuestionId(ID);
        return answerRequestDto;
    }

    private AnswerDto createAnswerDto() {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setQuestionId(ID);
        answerDto.setPoints(10);
        answerDto.setId(ID);
        answerDto.setCorrect(true);
        answerDto.setText("Basic text");
        return answerDto;
    }
}
