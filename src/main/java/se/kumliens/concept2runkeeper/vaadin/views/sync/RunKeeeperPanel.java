package se.kumliens.concept2runkeeper.vaadin.views.sync;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.VaadinSessionScope;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.ui.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atmosphere.config.service.Post;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.runkeeper.RunkeeperActivity;
import se.kumliens.concept2runkeeper.vaadin.converters.RunKeeperDateConverter;
import se.kumliens.concept2runkeeper.vaadin.converters.RunKeeperDistanceConverter;
import se.kumliens.concept2runkeeper.vaadin.converters.RunKeeperDurationConverter;

import javax.annotation.PostConstruct;

/**
 * Created by svante2 on 2017-04-17.
 */
@SpringComponent
@VaadinSessionScope
@Slf4j
@RequiredArgsConstructor
public class RunKeeeperPanel extends Panel {

    private BeanItemContainer<RunkeeperActivity> rkContainer = new BeanItemContainer<>(RunkeeperActivity.class);

    @PostConstruct
    public void createToContent() {
        MVerticalLayout root = new MVerticalLayout().withSpacing(true).withMargin(true);

        GeneratedPropertyContainer generatedPropertyContainer = new GeneratedPropertyContainer(rkContainer);

        //Add a generated column for the heart rate
        generatedPropertyContainer.addGeneratedProperty(RunkeeperActivity.HEART_RATE, new PropertyValueGenerator<Number[]>() {
            @Override
            public Number[] getValue(Item item, Object itemId, Object propertyId) {
                return new Number[]{1, 2, 3, 2, 2, 4, 5, 4, 2, 1, 2, 5, 4, 5, 5, 2, 3, 2, 3, 4, 5};
            }

            @Override
            public Class<Number[]> getType() {
                return Number[].class;
            }
        });

        com.vaadin.v7.ui.Grid grid = new com.vaadin.v7.ui.Grid(generatedPropertyContainer);
        grid.setWidth("100%");
        grid.setHeight("100%");
        grid.setSelectionMode(com.vaadin.v7.ui.Grid.SelectionMode.SINGLE);
        grid.setContainerDataSource(generatedPropertyContainer);
        grid.removeAllColumns();
        grid.addColumn(RunkeeperActivity.START_TIME).setHeaderCaption("Date").setConverter(new RunKeeperDateConverter()).setExpandRatio(1);
        grid.addColumn(RunkeeperActivity.DISTANCE).setHeaderCaption("Distance").setConverter(new RunKeeperDistanceConverter()).setExpandRatio(1);
        grid.addColumn(RunkeeperActivity.DURATION).setHeaderCaption("Time").setConverter(new RunKeeperDurationConverter()).setExpandRatio(1);
        grid.addColumn(RunkeeperActivity.TYPE).setHeaderCaption("(RunKeeper-) Type").setExpandRatio(1);
        //grid.addColumn(RunkeeperActivity.HEART_RATE).setRenderer(new SparklineRenderer()).setExpandRatio(2);

        root.expand(grid);
        setContent(root);
        setCaption("Your synchronized RunKeeper activities goes here");
    }

    public void newActivity(RunkeeperActivity rkActivity) {
        rkContainer.addItem(rkActivity);
    }
}
