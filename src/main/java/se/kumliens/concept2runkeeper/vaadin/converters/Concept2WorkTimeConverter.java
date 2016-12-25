package se.kumliens.concept2runkeeper.vaadin.converters;

import com.vaadin.data.util.converter.Converter;

import java.time.Duration;
import java.util.Locale;

/**
 * Convert a Concept2 time in seconds (as a double) to a presentation string
 *
 * Created by svante2 on 2016-12-25.
 */
public class Concept2WorkTimeConverter implements Converter<String, Double> {

    @Override
    public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale) throws ConversionException {
        return Double.valueOf(value);
    }

    @Override
    public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return Constants.formatDuration(Duration.ofSeconds(value.intValue()));
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
