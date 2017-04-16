package se.kumliens.concept2runkeeper.vaadin.converters;

import com.google.common.base.MoreObjects;
import com.vaadin.v7.data.util.converter.Converter;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 *
 * Created by svante2 on 2016-12-25.
 */
@Slf4j
public class Concept2DistanceConverter implements Converter<String, Double> {

    @Override
    public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return value != null ? value.toString() : "0.0";
    }

    @Override
    public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale) throws ConversionException {
        /*try {
            value = MoreObjects.firstNonNull(value, "0");
            DecimalFormat decimalFormat = new DecimalFormat(Constants.DISTANCE_PATTERN);
            return decimalFormat.format(Double.valueOf(value.replace(" ","").trim())) + " m";
        } catch (Exception e) {
            log.warn("Unable to parse {} to a number", value, e);
            return "n/a";
        }*/
        return Double.valueOf(value);
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
