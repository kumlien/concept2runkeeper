package se.kumliens.concept2runkeeper.vaadin.converters.v7;

import com.vaadin.v7.data.util.converter.Converter;
import se.kumliens.concept2runkeeper.vaadin.converters.Constants;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Converts a RunKeeper distance string to ui format.
 *
 * Created by svante2 on 2016-12-25.
 */
@Deprecated
public class RunKeeperDistanceConverter implements Converter<String, Double> {

    @Override
    public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale) throws ConversionException {
        return Double.valueOf(value.trim().replace("m","").replace(" ", ""));
    }

    @Override
    public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        DecimalFormat decimalFormat = new DecimalFormat(Constants.DISTANCE_PATTERN);
        return decimalFormat.format(value) + " m";
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
