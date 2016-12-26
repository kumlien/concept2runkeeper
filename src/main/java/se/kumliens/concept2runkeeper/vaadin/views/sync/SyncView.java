package se.kumliens.concept2runkeeper.vaadin.views.sync;

import com.google.common.base.MoreObjects;
import com.vaadin.data.Item;
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
import org.vaadin.viritin.layouts.MWindow;
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
import java.util.concurrent.atomic.LongAdder;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.vaadin.server.FontAwesome.EXCLAMATION_TRIANGLE;
import static com.vaadin.server.FontAwesome.LONG_ARROW_RIGHT;
import static com.vaadin.server.Sizeable.Unit.EM;
import static com.vaadin.shared.ui.label.ContentMode.HTML;
import static com.vaadin.ui.Alignment.*;
import static com.vaadin.ui.Grid.SelectionMode.MULTI;
import static com.vaadin.ui.Grid.SelectionMode.SINGLE;
import static com.vaadin.ui.themes.ValoTheme.*;
import static java.util.stream.Collectors.joining;
import static se.kumliens.concept2runkeeper.domain.Provider.CONCEPT2;
import static se.kumliens.concept2runkeeper.domain.Provider.RUNKEEPER;
import static se.kumliens.concept2runkeeper.vaadin.MainUI.link;

/**
 * The view used by a user to sync work outs.
 * <p>
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

    private BeanItemContainer<RunkeeperActivity> rkContainer = new BeanItemContainer<>(RunkeeperActivity.class);

    private BeanItemContainer<CsvActivity> concept2Container = new BeanItemContainer<>(CsvActivity.class);

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!ui.getUser().hasConnectionTo(RUNKEEPER)) {
            new MNotification("Since you don't have a connection setup with RunKeeper yet we have disable the<br>" +
                    "synchronization for now. Head over to the Settings page to fix this!").withHtmlContentAllowed(true).withStyleName(NOTIFICATION_FAILURE).withDelayMsec(3000).display();
        } else {
            new MNotification("Since you don't have a connection set up with Concept2 yet we can't fetch your data automatically.</br>" +
                    " You can still download your log file from Concept2 " + link("here", "http://log.concept2.com/history") + " and drop the file on the upper table.")
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

        MGrid<RunkeeperActivity> grid = new MGrid<>(RunkeeperActivity.class).withFullHeight().withFullWidth();
        grid.setSelectionMode(SINGLE);
        grid.setContainerDataSource(rkContainer);
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

            MGrid<CsvActivity> grid = new MGrid<>(CsvActivity.class).withFullWidth().withFullHeight();
            grid.setSelectionMode(MULTI);
            grid.setContainerDataSource(concept2Container);
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


    //Handle the actual sync process to RunKeeper
    private void setUpSyncClickHandler(MButton syncButton, MGrid<CsvActivity> concept2Grid) {
        syncButton.addClickListener(evt -> {
            Collection<CsvActivity> selectedRows = concept2Grid.getSelectedRowsWithType();
            if (selectedRows.isEmpty()) {
                log.warn("Sync button clicked without selected rows in the grid!");
                new MNotification("No selection detected (this is a bug). Try to re-select the row(s)").withStyleName(NOTIFICATION_ERROR).withDelayMsec(5000).display();
                return;
            }


            ProgressBar progressBar = new ProgressBar(0.0f);
            progressBar.setSizeFull();
            float percentPerRow = (float) 1 / selectedRows.size();

            MWindow progressWindow = new MWindow("Synchronizing with RunKeeper")
                    .withModal(true).withCenter().withContent(new MVerticalLayout().expand(progressBar).withWidth("100%")).withClosable(false).withWidth(40 , EM).withHeight(4, EM);
            ui.addWindow(progressWindow);

            new Thread(() -> {
                InternalRunKeeperData internalRunKeeperData = ui.getUser().getInternalRunKeeperData();
                ExternalRunkeeperData externalRunkeeperData = ui.getUser().getExternalRunkeeperData();
                List<String> newLocations = new ArrayList<>();
                final LongAdder skippedSync = new LongAdder();

                selectedRows.forEach(csvActivity -> {
                    //If there is a stored activity then pick that one, otherwise create a new one.
                    C2RActivity c2RActivity = MoreObjects.firstNonNull(c2RActivityRepo.findBySourceId(csvActivity.getDate()), C2RActivity.builder()
                            .userId(ui.getUser().getId())
                            .imported(Instant.now())
                            .source(CONCEPT2)
                            .sourceActivity(csvActivity)
                            .sourceId(csvActivity.getDate())
                            .build());

                    //Add the sync if not already there
                    if (c2RActivity.getSynchronizations().stream().filter(synchronization -> synchronization.getTarget() == RUNKEEPER).count() == 0) {
                        log.info("No sync to RunKeeper found, adding one...");
                        RecordActivityRequest request = createRecordActivityRequest(csvActivity, internalRunKeeperData, externalRunkeeperData);
                        URI activityLocation = runkeeperService.recordActivity(request, ui.getUser().getInternalRunKeeperData().getToken());
                        newLocations.add(ui.getUser().getExternalRunkeeperData().getProfile().getProfile() + activityLocation.toString());
                        RunkeeperActivity rkActivity = runkeeperService.getActivity(ui.getUser().getInternalRunKeeperData().getToken(), activityLocation);
                        c2RActivity.getSynchronizations().add(Synchronization.builder().date(Instant.now()).source(CONCEPT2).target(RUNKEEPER).targetActivity(rkActivity).sourceActivity(csvActivity).build());
                        rkContainer.addItem(rkActivity);
                        concept2Grid.getContainerDataSource().removeItem(csvActivity);
                        log.info("ok...");
                    } else {
                        log.info("Already synced to RunKeeper, skip new sync");
                        skippedSync.increment();
                    }

                    c2RActivity = c2RActivityRepo.save(c2RActivity);
                    log.info("Saved a new C2RActivity: {}", c2RActivity);
                    ui.access(()-> {
                        progressBar.setValue(progressBar.getValue() + percentPerRow);
                        ui.push();
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                //15 lines to display a message, wtf...
                String firstPart = "No activities created!";
                if (newLocations.size() == 1) {
                    firstPart = "One activity created at RunKeeper, you can find it at<br>";
                } else if (newLocations.size() > 1) {
                    firstPart = newLocations.size() + " activities created at RunKeeper. You can find them at <br>";
                }
                String locationsWithLineBreak = newLocations.stream().map(s -> "<a href=\"" + s + "\">" + s + "</a>").collect(joining("<br>")).toString().replaceAll("fitnessActivities", "activity");
                String skippedSyncMessage = "";
                if (skippedSync.intValue() == 1) {
                    skippedSyncMessage = "<br>" + "One activity was not sent to RunKeeper since this activity was already synced to RunKeeper";
                } else if (skippedSync.intValue() > 1) {
                    skippedSyncMessage = "<br>" + skippedSync.intValue() + " activities was not sent to RunKeeper since they were already synced to RunKeeper";
                }
                new MNotification(firstPart + locationsWithLineBreak + skippedSyncMessage).withHtmlContentAllowed(true).withStyleName(NOTIFICATION_SUCCESS).withDelayMsec(10000).display();
                ui.access(() -> {
                    concept2Grid.getSelectionModel().reset();
                    concept2Grid.clearSortOrder();
                    progressWindow.close();
                    ui.push();
                });
            }).start();
        });

    }

    private static RecordActivityRequest createRecordActivityRequest(CsvActivity csvActivity, InternalRunKeeperData internalRunKeeperData, ExternalRunkeeperData externalRunkeeperData) {
        Instant start;
        try {
            start = csvActivity.getDateAsInstant();
        } catch (ParseException e) {
            log.warn("Unable to parse the date from a CsvActivity into an instant. {}", csvActivity, e);
            start = Instant.now();
        }
        return RecordActivityRequest.builder()
                .equipment(Equipment.ROW_MACHINE)
                .notes(StringUtils.isEmpty(csvActivity.getComments()) ? internalRunKeeperData.getDefaultComment() : csvActivity.getComments())
                .postToFacebook(firstNonNull(internalRunKeeperData.getPostToFacebookOverride(), externalRunkeeperData.getSettings().isPostToFacebook()))
                .postToTwitter(firstNonNull(internalRunKeeperData.getPostToTwitterOverride(), externalRunkeeperData.getSettings().isPostToTwitter()))
                .startTime(start)
                .distance(csvActivity.getWorkDistance())
                .duration(Double.valueOf(csvActivity.getWorkTimeInSeconds()).intValue())
                .totalCalories(csvActivity.getCalPerHours())
                .type(ActivityType.ROWING).build();
    }

    private DragAndDropWrapper getDropAreaWithGrid(MGrid<CsvActivity> grid) {
        final CsvFileDropHandler dropBox = new CsvFileDropHandler(grid, activities -> {
            activities.forEach(csvActivity -> {
                grid.getContainerDataSource().addItem(csvActivity);
            });
            new MNotification(activities.size() + " activities found").withDelayMsec(2000).withStyleName(NOTIFICATION_SUCCESS).display();
        }, e -> {
            log.warn("Exception when uploading file", e);
            new MNotification("Unable to upload this file (" + e.getMessage() + ")").withDelayMsec(5000).withStyleName(NOTIFICATION_FAILURE).display();
        });
        dropBox.setSizeUndefined();
        return dropBox;
    }
}
