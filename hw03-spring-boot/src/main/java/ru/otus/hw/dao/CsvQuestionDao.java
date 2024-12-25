package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/
        String testFileName = fileNameProvider.getTestFileName();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resourceInputStream = classLoader.getResourceAsStream(testFileName);
        if (resourceInputStream == null) {
            throw new QuestionReadException("Resource not found: " + testFileName);
        }
        try (InputStreamReader inputStreamReader = new InputStreamReader(resourceInputStream)) {
            List<QuestionDto> questionDtos = new CsvToBeanBuilder(inputStreamReader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();
            return questionDtos.stream().map(QuestionDto::toDomainObject).toList();
        } catch (IOException e) {
            throw new QuestionReadException("Exception: ",  e);
        }
    }
}
