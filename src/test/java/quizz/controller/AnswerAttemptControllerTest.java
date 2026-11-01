package quizz.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import quizz.dto.answerattempt.AnswerAttemptDto;
import quizz.dto.answerattempt.AnswerAttemptRequestDto;
import quizz.exception.EntityNotFoundException;
import quizz.service.CustomUserDetailsService;
import quizz.service.interfaces.AnswerAttemptService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AnswerAttemptController.class)
@Import(SecurityConfig.class)
public class AnswerAttemptControllerTest {
    private static final Long ID = 1L;
    private static final Long INVALID_ID = 2L;
    private static final String USERNAME = "test@test.com";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnswerAttemptService answerAttemptService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void createAnswerAttempt_ValidAnswerAttemptRequestDto_ReturnsStatusCreated() throws Exception {
        //given
        AnswerAttemptRequestDto answerAttemptRequestDto = new AnswerAttemptRequestDto();
        answerAttemptRequestDto.setQuizAttemptId(ID);
        answerAttemptRequestDto.setAnswerId(ID);
        AnswerAttemptDto expected = createAnswerAttemptDto();

        when(answerAttemptService.createAnswerAttempt(answerAttemptRequestDto, USERNAME))
                .thenReturn(expected);

        //when & then
        MvcResult result = mockMvc.perform(post("/quiz-attempts/answers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerAttemptRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        AnswerAttemptDto actual = objectMapper.readValue(json, AnswerAttemptDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void createAnswerAttempt_InvalidAnswerAttemptRequestDto_ReturnsStatusBadRequest()
            throws Exception {
        //given
        AnswerAttemptRequestDto answerAttemptRequestDto = new AnswerAttemptRequestDto();

        //when & then
        mockMvc.perform(post("/quiz-attempts/answers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerAttemptRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "InvalidRole")
    void createAnswerAttempt_InvalidRole_ReturnsForbidden() throws Exception {
        //given
        AnswerAttemptRequestDto answerAttemptRequestDto = new AnswerAttemptRequestDto();
        answerAttemptRequestDto.setQuizAttemptId(ID);
        answerAttemptRequestDto.setAnswerId(ID);

        //when & then

        mockMvc.perform(post("/quiz-attempts/answers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerAttemptRequestDto)))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void getListOfAnswerAttempts_ValidIDAndUsername_ReturnsOk() throws Exception {
        // given
        AnswerAttemptDto answerAttemptDto = createAnswerAttemptDto();
        List<AnswerAttemptDto> list = new ArrayList<>();
        list.add(answerAttemptDto);

        when(answerAttemptService.getAnswerAttemptsByQuizAttemptId(ID, USERNAME))
                .thenReturn(list);

        // when & then
        mockMvc.perform(get("/quiz-attempts/answers/{quizAttemptId}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quizAttemptId")
                        .value(answerAttemptDto.getQuizAttemptId()));
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void getListOfAnswerAttempts_EmptyList_ReturnsOk() throws Exception {
        // given
        when(answerAttemptService.getAnswerAttemptsByQuizAttemptId(ID, USERNAME))
                .thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/quiz-attempts/answers/{quizAttemptId}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "InvalidRole")
    void getListOfAnswerAttempts_InvalidRole_ReturnsForbidden() throws Exception {
        //when & then
        mockMvc.perform(get("/quiz-attempts/answers/{quizAttemptId}", ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = "USER")
    void getListOfAnswerAttempts_InvalidId_ReturnsNotFound() throws Exception {
        //given
        when(answerAttemptService.getAnswerAttemptsByQuizAttemptId(INVALID_ID, USERNAME))
                .thenThrow(new EntityNotFoundException(""));

        //when & then
        mockMvc.perform(get("/quiz-attempts/answers/{quizAttemptId}", INVALID_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private AnswerAttemptDto createAnswerAttemptDto() {
        AnswerAttemptDto answerAttemptDto = new AnswerAttemptDto();
        answerAttemptDto.setCorrectAnswerIds(List.of());
        answerAttemptDto.setAnswerId(ID);
        answerAttemptDto.setCorrectAnswer(false);
        answerAttemptDto.setId(ID);
        answerAttemptDto.setFeedback("Basic feedback");
        answerAttemptDto.setQuizAttemptId(ID);
        answerAttemptDto.setQuestionId(ID);
        return answerAttemptDto;
    }
}
