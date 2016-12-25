package se.kumliens.concept2runkeeper.vaadin.converters;

import com.vaadin.data.util.converter.Converter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Locale;

/**
 * Converts runkeeper duration from seconds to our presentation format.
 * Created by svante2 on 2016-12-22.
 */
public class RunKeeperDurationConverter implements Converter<String, String> {

    @Override
    public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return String.valueOf(Duration.parse(value).getSeconds());
    }

    @Override
    public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if(value == null || value.isEmpty()) {
            value = "0";
        }
        Integer seconds = Integer.valueOf(value.trim());

        return Constants.formatDuration(Duration.ofSeconds(seconds));
    }

    @Override
    public Class<String> getModelType() {
        return String.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }


}
