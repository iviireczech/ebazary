package cz.ebazary.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.io.IOException;

public class CustomEntityMapper implements EntityMapper {

    private ObjectMapper objectMapper;

    public CustomEntityMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String mapToString(final Object object) throws IOException {

        return objectMapper.writeValueAsString(object);

    }

    @Override
    public <T> T mapToObject(final String source, Class<T> clazz) throws IOException {

        return objectMapper.readValue(source, clazz);

    }
}
