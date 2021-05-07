package net.digitallogic.UserApi.converters;

import net.digitallogic.UserApi.persistence.entity.auth.VerificationToken.TokenType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class TokenTypeConverter implements AttributeConverter<TokenType, Integer> {

    // Create a static map for faster lookup
    private static final Map<Integer, TokenType> intToTokenMap = Arrays.stream(TokenType.values())
            .collect(Collectors.toMap(token -> token.value, Function.identity()));

    @Override
    public Integer convertToDatabaseColumn(TokenType attribute) {
        return attribute.value;
    }

    @Override
    public TokenType convertToEntityAttribute(Integer dbData) {
        return intToTokenMap.get(dbData);
    }
}
