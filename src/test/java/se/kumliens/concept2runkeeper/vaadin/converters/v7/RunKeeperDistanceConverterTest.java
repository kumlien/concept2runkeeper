package se.kumliens.concept2runkeeper.vaadin.converters.v7;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

/**
 * Created by svante2 on 2016-12-25.
 */
public class RunKeeperDistanceConverterTest {

    private RunKeeperDistanceConverter converter = new RunKeeperDistanceConverter();

    @Test
    public void convertToModel() throws Exception {
        Double aDouble = converter.convertToModel("124 m", Double.class, Locale.getDefault());
    }

    @Test
    public void convertToPresentationRoundUp() throws Exception {
        Double input = Double.valueOf("123.656");
        String presentation = converter.convertToPresentation(input, String.class, Locale.getDefault());
        Assert.assertEquals("124 m", presentation);
    }

    @Test
    public void convertToPresentationRoundDown() throws Exception {
        Double input = Double.valueOf("123.456");
        String presentation = converter.convertToPresentation(input, String.class, Locale.getDefault());
        Assert.assertEquals("123 m", presentation);
    }

}