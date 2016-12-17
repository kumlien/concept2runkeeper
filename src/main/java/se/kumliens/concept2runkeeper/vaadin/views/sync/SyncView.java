package se.kumliens.concept2runkeeper.vaadin.views.sync;

import com.google.common.base.MoreObjects;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.ui.MNotification;
import se.kumliens.concept2runkeeper.concept2.CsvActivity;
import se.kumliens.concept2runkeeper.runkeeper.*;
import se.kumliens.concept2runkeeper.vaadin.MainUI;

import javax.annotation.PostConstruct;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.vaadin.server.FontAwesome.EXCLAMATION_TRIANGLE;
import static com.vaadin.server.FontAwesome.LONG_ARROW_RIGHT;
import static com.vaadin.shared.ui.label.ContentMode.HTML;
import static com.vaadin.ui.Alignment.*;
import static com.vaadin.ui.Grid.SelectionMode.MULTI;
import static com.vaadin.ui.Grid.SelectionMode.SINGLE;
import static com.vaadin.ui.themes.ValoTheme.NOTIFICATION_SUCCESS;
import static java.util.stream.Collectors.joining;
import static se.kumliens.concept2runkeeper.domain.Provider.CONCEPT2;
import static se.kumliens.concept2runkeeper.vaadin.MainUI.link;

/**
 * The view used by a user to sync work outs.
 *
 * <p>
 * Created by svante2 on 2016-11-30.
 */
@SpringView
@RequiredArgsConstructor
@Slf4j
public class SyncView extends MVerticalLayout implements View {

    private final MainUI ui;

    private final RunkeeperService runkeeperService;

    private final RunkeeperProps runkeeperProps;

    private BeanItemContainer<RunkeeperActivity> toContainer;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    @PostConstruct
    public void init() {
        Label label = new Label("For now we only support synchronizing from <a href=\"http://log.concept2.com/\">Concept2</a> to " +
                "<a href=\"http://www.runkeeper.com/\">RunKeeper</a>.");
        label.setContentMode(HTML);
        label.setSizeUndefined();
        add(label, TOP_CENTER);


        MVerticalLayout fromContent = createFromContent().withMargin(false).withSpacing(false);
        fromContent.setSizeFull();
        //fromContent.withStyleName(LAYOUT_WELL);

        MVerticalLayout toContent = createToContent().withMargin(false).withSpacing(false);
        toContent.setSizeFull();
        //toContent.withStyleName(LAYOUT_WELL);

        MHorizontalLayout allContent = new MHorizontalLayout(fromContent, toContent);
        allContent.setSizeFull();
        expand(allContent);
        setSizeFull();
        //addStyleName(LAYOUT_WELL);
        withMargin(false);
        withSpacing(false);
    }


    //Create the content representing where we synchronize to
    private MVerticalLayout createToContent() {
        Header header = new Header("RunKeeper").setHeaderLevel(3);
        MVerticalLayout layout = new MVerticalLayout();
        layout.add(header, MIDDLE_LEFT);

        toContainer = new BeanItemContainer<RunkeeperActivity>(RunkeeperActivity.class);
        MGrid<RunkeeperActivity> grid = new MGrid<>(RunkeeperActivity.class).withFullHeight().withFullWidth();
        grid.setSelectionMode(SINGLE);
        grid.setContainerDataSource(toContainer);
        grid.removeAllColumns();
        grid.addColumn(RunkeeperActivity.START_TIME).setHeaderCaption("Date");
        grid.addColumn(RunkeeperActivity.DISTANCE).setHeaderCaption("Distance");
        grid.addColumn(RunkeeperActivity.DURATION).setHeaderCaption("Time");
        grid.addColumn(RunkeeperActivity.TYPE).setHeaderCaption("Type");

        layout.expand(grid);

        return layout;
    }


    //Create the content representing where we synchronize from. Hardwire to concept2 for now
    private MVerticalLayout createFromContent() {
        Header header = new Header("Concept2").setHeaderLevel(3);
        header.setSizeUndefined();

        MVerticalLayout layout = new MVerticalLayout();
        MHorizontalLayout topLayout = new MHorizontalLayout(header).withSpacing(true).withFullWidth();
        topLayout.withAlign(header, MIDDLE_LEFT);
        layout.add(topLayout);
        if (ui.getUser().hasConnectionTo(CONCEPT2)) {
            layout.add(new Label("Should not happen until we have set up connection with Concept2..."));
        } else {
            displayNoConcept2ConnectionMessage();
            MButton syncButton = new MButton("Send to RunKeeper").withIcon(LONG_ARROW_RIGHT).withStyleName("friendly");
            syncButton.setSizeUndefined();
            syncButton.setEnabled(false);

            topLayout.add(syncButton);
            topLayout.withAlign(syncButton, Alignment.MIDDLE_CENTER);

            BeanItemContainer<CsvActivity> container = new BeanItemContainer<>(CsvActivity.class);
            MGrid<CsvActivity> grid = new MGrid<>(CsvActivity.class).withFullWidth().withFullHeight();
            grid.setSelectionMode(MULTI);
            grid.setContainerDataSource(container);
            grid.removeAllColumns();
            grid.addColumn("date").setHeaderCaption("Date");
            grid.addColumn("workDistance").setHeaderCaption("Distance");
            grid.addColumn("formattedWorkTime").setHeaderCaption("Time");
            grid.addColumn("pace").setHeaderCaption("Pace");
            grid.addColumn("type").setHeaderCaption("Type");
            grid.addSelectionListener(evt -> {
                syncButton.setEnabled(!grid.getSelectedRows().isEmpty());
            });

            syncButton.addClickListener(evt -> {
                Collection selectedRows = grid.getSelectedRows();
                if(selectedRows.isEmpty()) {
                    log.warn("Sync button clicked without selected rows in the grid!");
                    Notification.show("No selection detected (this is a bug). Try to re-select the row(s)");
                    return;
                }
                InternalRunKeeperData internalRunKeeperData = ui.getUser().getInternalRunKeeperData();
                ExternalRunkeeperData externalRunkeeperData = ui.getUser().getExternalRunkeeperData();
                List<String> locations = new ArrayList<>();

                for(Iterator iter = selectedRows.iterator(); iter.hasNext();) {
                    CsvActivity c2Activity = (CsvActivity) iter.next();
                    Instant start;
                    try {
                        start = c2Activity.getDateAsInstant();
                    } catch (ParseException e) {
                        log.warn("Unable to parse the date from a CsvActivity into an instant", e);
                        return;
                    }
                    RecordActivityRequest request = RecordActivityRequest.builder()
                            .equipment(Equipment.ROW_MACHINE)
                            .notes(StringUtils.isEmpty(c2Activity.getComments()) ? internalRunKeeperData.getDefaultComment():c2Activity.getComments())
                            .postToFacebook(firstNonNull(internalRunKeeperData.getPostToFacebookOverride(), externalRunkeeperData.getSettings().isPostToFacebook()))
                            .postToTwitter(firstNonNull(internalRunKeeperData.getPostToTwitterOverride(), externalRunkeeperData.getSettings().isPostToTwitter()))
                            .startTime(start)
                            .distance(c2Activity.getWorkDistance())
                            .duration(Double.valueOf(c2Activity.getWorkTimeInSeconds()).intValue())
                            .totalCalories(c2Activity.getCalPerHours())
                            .type(ActivityType.ROWING).build();
                    URI activityLocation = runkeeperService.recordActivity(request, ui.getUser().getInternalRunKeeperData().getToken());
                    locations.add(ui.getUser().getExternalRunkeeperData().getProfile().getProfile() + activityLocation.toString());
                    RunkeeperActivity rkActivity = runkeeperService.getActivity(ui.getUser().getInternalRunKeeperData().getToken(), activityLocation);
                    toContainer.addItem(rkActivity);
                    grid.getContainerDataSource().removeItem(c2Activity);
                }
                String locationsWithLineBreak = locations.stream().map(s -> "<a href=\"" + s + "\">" + s + "</a>").collect(joining("<br>")).toString().replaceAll("fitnessActivities", "activity");
                String firstPart = locations.size() == 1 ? "activity created at RunKeeper, you can find it at<br>" : "activities created at RunKeeper. You can find them at <br>";
                new MNotification(locations.size() + firstPart + locationsWithLineBreak)
                        .withHtmlContentAllowed(true).withStyleName(NOTIFICATION_SUCCESS).withDelayMsec(10000).display();

            });

            DragAndDropWrapper dropArea = getDropAreaWithGrid(grid);
            dropArea.setSizeFull();
            layout.add(dropArea);
            layout.setExpandRatio(dropArea, 1.0f);
        }

        return layout;
    }

    private DragAndDropWrapper getDropAreaWithGrid(MGrid<CsvActivity> grid) {
        final CsvFileDropHandler dropBox = new CsvFileDropHandler(grid, activities -> {
            activities.forEach(csvActivity ->  {
                grid.getContainerDataSource().addItem(csvActivity);
            });
            Notification.show(activities.size() + " activities found");
        }, e -> {
            log.warn("Exception when uploading file", e);
            Notification.show("Unable to upload this file (" + e.getMessage() + ")");
        });
        dropBox.setSizeUndefined();
        return dropBox;
    }

    private void displayNoConcept2ConnectionMessage() {
        MNotification notification = new MNotification("Since you don't have a connection set up with Concept2 yet we can't fetch your data automatically.</br>" +
                " You can still download your log file from Concept2 " + link("here", "http://log.concept2.com/history") + " and drop the file on the table below.")
                .withHtmlContentAllowed(true).withStyleName(ValoTheme.NOTIFICATION_WARNING).withIcon(EXCLAMATION_TRIANGLE).withDelayMsec(10000).display();
    }
}
