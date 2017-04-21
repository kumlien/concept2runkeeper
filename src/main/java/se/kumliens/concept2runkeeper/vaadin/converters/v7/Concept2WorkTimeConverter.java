package se.kumliens.concept2runkeeper.vaadin.converters.v7;

import com.vaadin.v7.data.util.converter.Converter;
import se.kumliens.concept2runkeeper.vaadin.converters.Constants;

import java.time.Duration;
import java.util.Locale;

/**
 * Convert a Concept2 time in seconds (as a double) to a presentation string
 *
 * Created by svante2 on 2016-12-25.
 */
@Deprecated
public class Concept2WorkTimeConverter implements Converter<String, Double> {

    @Override
    public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale) throws ConversionException {
        return Double.valueOf(value);
    }

    @Override
    public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return Constants.formatDuration(Duration.ofSeconds(value != null ? value.intValue() : 0));
    }

    @Override
    public Class<Double> getModelType() {
        return Double.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
