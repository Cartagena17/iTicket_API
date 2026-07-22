package iTicket.Douglas.Utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class BooleanToCharConverter implements AttributeConverter<Boolean, String> {
    @Override
    public String convertToDatabaseColumn(Boolean valor) {
        if (valor == null) {
            return null;
        }
        return valor ? "T" : "F";
    }
    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return "T".equalsIgnoreCase(dbData);
    }
}
