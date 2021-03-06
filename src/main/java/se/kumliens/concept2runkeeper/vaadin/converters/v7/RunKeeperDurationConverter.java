package se.kumliens.concept2runkeeper.vaadin.converters.v7;

import com.vaadin.v7.data.util.converter.Converter;
import se.kumliens.concept2runkeeper.vaadin.converters.Constants;

import java.time.Duration;
import java.util.Locale;

/**
 * Converts runkeeper duration from seconds to our presentation format.
 * Created by svante2 on 2016-12-22.
 */
@Deprecated
public class RunKeeperDurationConverter implements Converter<String, Double> {

    @Override
    public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale) throws ConversionException {
        return Double.valueOf(Duration.parse(value).getSeconds());
    }

    @Override
    public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if(value == null) {
            value = 0.0;
        }

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
