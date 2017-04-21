package se.kumliens.concept2runkeeper.vaadin.views.sync;

import com.vaadin.data.Binder;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.VaadinSessionScope;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.DateRenderer;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.viritin.layouts.MVerticalLayout;
import se.kumliens.concept2runkeeper.domain.runkeeper.RunkeeperActivity;
import se.kumliens.concept2runkeeper.vaadin.converters.Constants;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.vaadin.ui.Grid.SelectionMode.SINGLE;

/**
 * Created by svante2 on 2017-04-17.
 */
@SpringComponent
@VaadinSessionScope
@Slf4j
public class RunKeeeperPanel extends Panel {

    private final List<RunkeeperActivity> activities = new ArrayList<>();

    ListDataProvider<RunkeeperActivity> dataProvider = DataProvider.ofCollection(activities);

    @PostConstruct
    public void createToContent() {
        MVerticalLayout root = new MVerticalLayout().withSpacing(true).withMargin(true);

        Grid<RunkeeperActivity> grid = new Grid<>(dataProvider);
        DecimalFormat decimalFormat = new DecimalFormat(Constants.DISTANCE_PATTERN);

        grid.setWidth("100%");
        grid.setHeight("100%");
        grid.setSelectionMode(SINGLE);
        grid.addColumn(RunkeeperActivity::getStartTimeAsDate).setRenderer(new DateRenderer()).setCaption("Start time").setWidthUndefined();
        grid.addColumn(activity -> decimalFormat.format(activity.getDistance()) + " m").setCaption("Distance").setWidthUndefined();

        grid.addColumn(activity -> Constants.formatDuration(Duration.ofSeconds(activity.getDuration().intValue()))).setCaption("Time");
        grid.addColumn(RunkeeperActivity::getType).setCaption("(RunKeeper-) Type");
        //grid.addColumn(RunkeeperActivity.HEART_RATE).setRenderer(new SparkLine()).setExpandRatio(2);
        
        root.expand(grid);
        setContent(root);
        setCaption("Your synchronized RunKeeper activities goes here");
    }

    public void newActivity(RunkeeperActivity rkActivity) {
        activities.add(rkActivity);
        dataProvider.refreshAll();
    }
}
