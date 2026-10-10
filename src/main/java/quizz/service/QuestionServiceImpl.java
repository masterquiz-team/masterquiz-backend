package quizz.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quizz.dto.question.QuestionDto;
import quizz.dto.question.QuestionRequestDto;
import quizz.dto.question.UpdateQuestionDto;
import quizz.exception.EntityFoundException;
import quizz.exception.EntityNotFoundException;
import quizz.mapper.QuestionMapper;
import quizz.model.Category;
import quizz.model.Question;
import quizz.repository.CategoryRepository;
import quizz.repository.QuestionRepository;
import quizz.service.interfaces.QuestionService;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionMapper mapper;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public QuestionDto createQuestion(QuestionRequestDto dto) {
        findByText(dto.getText());
        Category category = findByCategoryId(dto.getCategoryId());
        Question question = mapper.toModel(dto);
        question.setCategory(category);
        Question savedQuestion = questionRepository.save(question);
        return mapper.toDto(savedQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionDto getQuestion(Long id) {
        return mapper.toDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionDto> getAllQuestionsByCategory(String categoryName) {
        if (categoryRepository.findByName(categoryName).isEmpty()) {
            throw new EntityNotFoundException("Category with name: " + categoryName + " doesn't exist");
        }
        return questionRepository.findAll().stream()
                .filter(question -> question.getCategory().getName().equals(categoryName))
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public QuestionDto updateQuestion(UpdateQuestionDto dto) {
        Question question = findById(dto.getId());
        Category category = findByCategoryId(dto.getCategoryId());
        question.setCategory(category);
        mapper.update(dto, question);
        return mapper.toDto(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        Question question = findById(id);
        questionRepository.delete(question);
    }

    private void findByText(String text) {
        if (questionRepository.findByText(text).isPresent()) {
            throw new EntityFoundException("Question with id: " + text + " already exist");
        }
    }

    private Question findById(Long id) {
        return questionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Question with id: " + id + " doesn't exist"));
    }

    private Category findByCategoryId(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category with id: "
                        + id + " doesn't exist"));
    }
}
