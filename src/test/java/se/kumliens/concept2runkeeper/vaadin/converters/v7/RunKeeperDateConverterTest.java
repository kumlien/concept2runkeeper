package se.kumliens.concept2runkeeper.vaadin.converters.v7;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

/**
 * Created by svante2 on 2016-12-25.
 */
public class RunKeeperDateConverterTest {

    private RunKeeperDateConverter converter = new RunKeeperDateConverter();


    @Test
    public void convertToModel() throws Exception {


    }

    @Test
    public void convertToPresentation() throws Exception {
        String dateString = "Mon, 12 Dec 2016 19:32:00";
        String expectedConverted = "2016-12-12 19:32:00";
        String converted = converter.convertToPresentation(dateString, String.class, Locale.ENGLISH);
        System.out.println(converted);
        Assert.assertEquals(expectedConverted, converted);

    }

}