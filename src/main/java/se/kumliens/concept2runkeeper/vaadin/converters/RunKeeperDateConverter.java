package se.kumliens.concept2runkeeper.vaadin.converters;

import com.vaadin.v7.data.util.converter.Converter;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Converts from a RunKeeper date string (format like Mon, 12 dec 2016 19:32:00) to a ui date string
 *
 * Created by svante2 on 2016-12-25.
 */
@Slf4j
public class RunKeeperDateConverter implements Converter<String, String> {

    @Override
    public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        //Mon, 12 Dec 2016 19:32:00
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
        try {
            Date date = df.parse(value);
            return Constants.formatDate(date);
        } catch (ParseException e) {
            log.warn("Unable to parse string value '{}' to a date", value, e);
            return "n/a";
        }
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
