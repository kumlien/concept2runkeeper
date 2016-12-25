package se.kumliens.concept2runkeeper.vaadin.views.sync;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MCheckBox;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.ui.MNotification;
import se.kumliens.concept2runkeeper.concept2.CsvActivity;
import se.kumliens.concept2runkeeper.domain.C2RActivity;
import se.kumliens.concept2runkeeper.domain.Synchronization;
import se.kumliens.concept2runkeeper.repos.C2RActivityRepo;
import se.kumliens.concept2runkeeper.runkeeper.*;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.converters.*;

import javax.annotation.PostConstruct;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.vaadin.server.FontAwesome.EXCLAMATION_TRIANGLE;
import static com.vaadin.server.FontAwesome.LONG_ARROW_RIGHT;
import static com.vaadin.shared.ui.label.ContentMode.HTML;
import static com.vaadin.ui.Alignment.*;
import static com.vaadin.ui.Grid.SelectionMode.MULTI;
import static com.vaadin.ui.Grid.SelectionMode.SINGLE;
import static com.vaadin.ui.themes.ValoTheme.NOTIFICATION_FAILURE;
import static com.vaadin.ui.themes.ValoTheme.NOTIFICATION_SUCCESS;
import static com.vaadin.ui.themes.ValoTheme.NOTIFICATION_WARNING;
import static java.util.stream.Collectors.joining;
import static se.kumliens.concept2runkeeper.domain.Provider.CONCEPT2;
import static se.kumliens.concept2runkeeper.domain.Provider.RUNKEEPER;
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

    private final C2RActivityRepo c2RActivityRepo;

    private BeanItemContainer<RunkeeperActivity> toContainer;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if(!ui.getUser().hasConnectionTo(RUNKEEPER)) {
            new MNotification("Since you don't have a connection setup with RunKeeper yet we have disable the<br>" +
                    "synchronization for now. Head over to the Settings page to fix this!").withHtmlContentAllowed(true).withStyleName(NOTIFICATION_FAILURE).withDelayMsec(3000).display();
        } else {
            new MNotification("Since you don't have a connection set up with Concept2 yet we can't fetch your data automatically.</br>" +
                    " You can still download your log file from Concept2 " + link("here", "http://log.concept2.com/history") + " and drop the file on the left table below.")
                    .withHtmlContentAllowed(true).withStyleName(NOTIFICATION_WARNING).withIcon(EXCLAMATION_TRIANGLE).withDelayMsec(5000).display();
        }
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

        MVerticalLayout toContent = createToContent().withMargin(false).withSpacing(false);
        toContent.setSizeFull();
        Component splitPanel = new VerticalSplitPanel(fromContent, toContent);
        splitPanel.setSizeFull();

        MHorizontalLayout allContent = new MHorizontalLayout(splitPanel);
        allContent.setSizeFull();
        expand(allContent);
        withMargin(true);
        withSpacing(true);
    }


    //Create the content representing where we synchronize to
    private MVerticalLayout createToContent() {
        Header header = new Header("RunKeeper").setHeaderLevel(3);
        MVerticalLayout layout = new MVerticalLayout();
        layout.add(header, MIDDLE_LEFT);

        toContainer = new BeanItemContainer<>(RunkeeperActivity.class);
        MGrid<RunkeeperActivity> grid = new MGrid<>(RunkeeperActivity.class).withFullHeight().withFullWidth();
        grid.setSelectionMode(SINGLE);
        grid.setContainerDataSource(toContainer);
        grid.removeAllColumns();
        grid.addColumn(RunkeeperActivity.START_TIME).setHeaderCaption("Date").setConverter(new RunKeeperDateConverter());
        grid.addColumn(RunkeeperActivity.DISTANCE).setHeaderCaption("Distance").setConverter(new RunKeeperDistanceConverter());
        grid.addColumn(RunkeeperActivity.DURATION).setHeaderCaption("Time").setConverter(new RunKeeperDurationConverter());
        grid.addColumn(RunkeeperActivity.TYPE).setHeaderCaption("(RunKeeper-) Type");

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
            MButton syncButton = new MButton("Send to RunKeeper").withIcon(LONG_ARROW_RIGHT).withStyleName("friendly");
            syncButton.setEnabled(ui.getUser().hasConnectionTo(RUNKEEPER));
            syncButton.setSizeUndefined();
            syncButton.setEnabled(false);

            MCheckBox forceSync = new MCheckBox("Force synchronization").withBlurListener(evt -> {

            });

            topLayout.add(syncButton);
            topLayout.withAlign(syncButton, Alignment.MIDDLE_CENTER);

            BeanItemContainer<CsvActivity> container = new BeanItemContainer<>(CsvActivity.class);
            MGrid<CsvActivity> grid = new MGrid<>(CsvActivity.class).withFullWidth().withFullHeight();
            grid.setSelectionMode(MULTI);
            grid.setContainerDataSource(container);
            grid.removeAllColumns();
            grid.addColumn("date").setHeaderCaption("Date");
            grid.addColumn("workDistance").setHeaderCaption("Distance").setConverter(new Concept2DistanceConverter());
            grid.addColumn("workTimeInSeconds").setHeaderCaption("Time").setConverter(new Concept2WorkTimeConverter());
            grid.addColumn("pace").setHeaderCaption("Pace");
            grid.addColumn("type").setHeaderCaption("(Concept2-) Type");
            grid.addSelectionListener(evt -> {
                syncButton.setEnabled(!grid.getSelectedRows().isEmpty());
            });

            setUpSyncClickHandler(syncButton, grid);

            DragAndDropWrapper dropArea = getDropAreaWithGrid(grid);
            dropArea.setSizeFull();
            layout.add(dropArea);
            layout.setExpandRatio(dropArea, 1.0f);
        }

        return layout;
    }


    private void setUpSyncClickHandler(MButton syncButton, MGrid<CsvActivity> grid) {
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
                CsvActivity csvActivity = (CsvActivity) iter.next();
                Instant start;
                try {
                    start = csvActivity.getDateAsInstant();
                } catch (ParseException e) {
                    log.warn("Unable to parse the date from a CsvActivity into an instant. {}",csvActivity, e);
                    new MNotification("We can't parse the date from the csv activity with raw date: {}", csvActivity.getDate()).withStyleName(ValoTheme.NOTIFICATION_FAILURE).withDelayMsec(2500).display();
                    return;
                }
                RecordActivityRequest request = RecordActivityRequest.builder()
                        .equipment(Equipment.ROW_MACHINE)
                        .notes(StringUtils.isEmpty(csvActivity.getComments()) ? internalRunKeeperData.getDefaultComment():csvActivity.getComments())
                        .postToFacebook(firstNonNull(internalRunKeeperData.getPostToFacebookOverride(), externalRunkeeperData.getSettings().isPostToFacebook()))
                        .postToTwitter(firstNonNull(internalRunKeeperData.getPostToTwitterOverride(), externalRunkeeperData.getSettings().isPostToTwitter()))
                        .startTime(start)
                        .distance(csvActivity.getWorkDistance())
                        .duration(Double.valueOf(csvActivity.getWorkTimeInSeconds()).intValue())
                        .totalCalories(csvActivity.getCalPerHours())
                        .type(ActivityType.ROWING).build();
                URI activityLocation = runkeeperService.recordActivity(request, ui.getUser().getInternalRunKeeperData().getToken());
                locations.add(ui.getUser().getExternalRunkeeperData().getProfile().getProfile() + activityLocation.toString());
                RunkeeperActivity rkActivity = runkeeperService.getActivity(ui.getUser().getInternalRunKeeperData().getToken(), activityLocation);
                toContainer.addItem(rkActivity);
                grid.getContainerDataSource().removeItem(csvActivity);

                C2RActivity c2RActivity = C2RActivity.builder()
                        .userId(ui.getUser().getId())
                        .imported(Instant.now())
                        .source(CONCEPT2)
                        .sourceActivity(csvActivity)
                        .sourceId(csvActivity.getDate())
                        .build();
                c2RActivity.getSynchronizations().add(Synchronization.builder().date(Instant.now()).source(CONCEPT2).target(RUNKEEPER).targetActivity(rkActivity).sourceActivity(csvActivity).build());
                c2RActivity = c2RActivityRepo.save(c2RActivity);
                log.info("Saved a new C2RActivity: {}", c2RActivity);
            }
            String locationsWithLineBreak = locations.stream().map(s -> "<a href=\"" + s + "\">" + s + "</a>").collect(joining("<br>")).toString().replaceAll("fitnessActivities", "activity");
            String firstPart = locations.size() == 1 ? "activity created at RunKeeper, you can find it at<br>" : "activities created at RunKeeper. You can find them at <br>";
            new MNotification(locations.size() + firstPart + locationsWithLineBreak)
                    .withHtmlContentAllowed(true).withStyleName(NOTIFICATION_SUCCESS).withDelayMsec(10000).display();

        });
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
}
