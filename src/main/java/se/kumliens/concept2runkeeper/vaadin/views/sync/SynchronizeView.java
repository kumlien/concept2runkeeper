package se.kumliens.concept2runkeeper.vaadin.views.sync;

import com.google.common.base.MoreObjects;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Grid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;
import org.vaadin.viritin.ui.MNotification;
import org.vaadin.viritin.v7.label.MLabel;
import se.kumliens.concept2runkeeper.domain.C2RActivity;
import se.kumliens.concept2runkeeper.domain.Synchronization;
import se.kumliens.concept2runkeeper.domain.concept2.Concept2CsvActivity;
import se.kumliens.concept2runkeeper.domain.runkeeper.*;
import se.kumliens.concept2runkeeper.repos.C2RActivityRepo;
import se.kumliens.concept2runkeeper.runkeeper.RecordActivityRequest;
import se.kumliens.concept2runkeeper.services.NoSuchActivityException;
import se.kumliens.concept2runkeeper.services.RunkeeperService;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.converters.RunKeeperDateConverter;
import se.kumliens.concept2runkeeper.vaadin.converters.RunKeeperDistanceConverter;
import se.kumliens.concept2runkeeper.vaadin.converters.RunKeeperDurationConverter;
import se.kumliens.concept2runkeeper.vaadin.events.ActivitySyncEvent;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.vaadin.server.Sizeable.Unit.EM;
import static com.vaadin.ui.themes.ValoTheme.*;
import static java.util.stream.Collectors.toList;
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
public class SynchronizeView extends MVerticalLayout implements View {

    private final MainUI ui;

    private final RunkeeperService runkeeperService;

    private final C2RActivityRepo c2RActivityRepo;

    private final Concept2Panel concept2Panel;

    private final EventBus.ApplicationEventBus eventBus;

    private BeanItemContainer<RunkeeperActivity> rkContainer = new BeanItemContainer<>(RunkeeperActivity.class);

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!ui.getUser().hasConnectionTo(RUNKEEPER)) {
            new MNotification("Since you don't have a connection setup with RunKeeper yet we have disabled the<br>" +
                    "synchronization for now. Head over to the Settings page to fix this!").withHtmlContentAllowed(true).withStyleName(NOTIFICATION_FAILURE).withDelayMsec(3000).display();
        } else if (!ui.getUser().hasConnectionTo(CONCEPT2)) {
            new MNotification("Since you don't have a connection set up with Concept2 yet we can't fetch your data automatically.</br>" +
                    " You can still download your log file from Concept2 " + link("here", "http://log.concept2.com/history") + " and drop the file on the upper table.")
                    .withHtmlContentAllowed(true).withStyleName(NOTIFICATION_CLOSABLE).withDelayMsec(5000).display();
        }
    }

    @PostConstruct
    public void init() {
        MLabel label = new MLabel("Synchronize your activities from <a href=\"http://log.concept2.com/\">Concept2</a> to " +
                "<a href=\"http://www.runkeeper.com/\">RunKeeper</a>.");
        label.setContentMode(ContentMode.HTML);
        label.setSizeUndefined();

        concept2Panel.createContent();
        Panel fromContent = concept2Panel;
        fromContent.setSizeFull();

        Panel toContent = createToContent();
        toContent.setSizeFull();

        VerticalSplitPanel splitPanel = new VerticalSplitPanel(fromContent, toContent);
        splitPanel.setSizeFull();

        MHorizontalLayout allContent = new MHorizontalLayout(splitPanel).withMargin(true).withSpacing(true);
        allContent.setSizeFull();
        expand(allContent);
        withMargin(false);
        withSpacing(true);

        concept2Panel.setSynchronizeView(this);
    }


    //Create the content representing where we synchronize to
    private Panel createToContent() {
        MVerticalLayout layout = new MVerticalLayout().withSpacing(true).withMargin(true);

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

        Grid grid = new Grid(generatedPropertyContainer);
        grid.setWidth("100%");
        grid.setHeight("100%");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setContainerDataSource(generatedPropertyContainer);
        grid.removeAllColumns();
        grid.addColumn(RunkeeperActivity.START_TIME).setHeaderCaption("Date").setConverter(new RunKeeperDateConverter()).setExpandRatio(1);
        grid.addColumn(RunkeeperActivity.DISTANCE).setHeaderCaption("Distance").setConverter(new RunKeeperDistanceConverter()).setExpandRatio(1);
        grid.addColumn(RunkeeperActivity.DURATION).setHeaderCaption("Time").setConverter(new RunKeeperDurationConverter()).setExpandRatio(1);
        grid.addColumn(RunkeeperActivity.TYPE).setHeaderCaption("(RunKeeper-) Type").setExpandRatio(1);
        //grid.addColumn(RunkeeperActivity.HEART_RATE).setRenderer(new SparklineRenderer()).setExpandRatio(2);

        layout.expand(grid);
        return new MPanel("Your synchronized RunKeeper activities goes here").withContent(layout);
    }

    private Optional<RunkeeperActivity> updateActivity(RunkeeperActivity runkeeperActivity) {
        try {
            return Optional.of(runkeeperService.updateActivity(runkeeperActivity, ui.getUser().getInternalRunKeeperData().getToken()));
        } catch (NoSuchActivityException e) {
            new MNotification("No corresponding activity found at RunKeeper, please synchronize the activity and try again").withStyleName(NOTIFICATION_WARNING).withDelayMsec(3000).display();
        }
        return Optional.empty();
    }

    boolean doSync(Concept2CsvActivity csvActivity, boolean forced) {
        MWindow progressWindow = new MWindow("Creating the activity at RunKeeper")
                .withModal(true).withCenter().withContent(new MVerticalLayout().withWidth("100%")).withClosable(false).withResizable(false).withWidth(40, EM).withHeight(4, EM);
        ui.addWindow(progressWindow);

        InternalRunKeeperData internalRunKeeperData = ui.getUser().getInternalRunKeeperData();
        ExternalRunkeeperData externalRunkeeperData = ui.getUser().getExternalRunkeeperData();
        String newLocation = null;
        //If there is a stored activity then pick that one, otherwise create a new one.
        C2RActivity c2RActivity = MoreObjects.firstNonNull(c2RActivityRepo.findBySourceId(csvActivity.getDate()), C2RActivity.builder()
                .userId(ui.getUser().getEmail())
                .imported(Instant.now())
                .source(CONCEPT2)
                .sourceActivity(csvActivity)
                .sourceId(csvActivity.getDate())
                .build());

        //Add the sync if not already there or if forced
        if (forced || c2RActivity.getSynchronizations().stream().filter(synchronization -> synchronization.getTarget() == RUNKEEPER).count() == 0) {
            try {
                log.debug("No sync to RunKeeper found or forced sync, let's do it");
                RecordActivityRequest request = createRecordActivityRequest(csvActivity, internalRunKeeperData, externalRunkeeperData);
                URI activityLocation = runkeeperService.recordActivity(request, ui.getUser().getInternalRunKeeperData().getToken());
                newLocation = (ui.getUser().getExternalRunkeeperData().getProfile().getProfile() + activityLocation.toString()).replaceAll("fitnessActivities", "activity");
                RunkeeperActivity rkActivity = runkeeperService.getActivity(ui.getUser().getInternalRunKeeperData().getToken(), activityLocation);
                c2RActivity.getSynchronizations().add(Synchronization.builder().date(Instant.now()).source(CONCEPT2).target(RUNKEEPER).targetActivity(rkActivity).build());
                rkContainer.addItem(rkActivity);
                c2RActivity = c2RActivityRepo.save(c2RActivity);
                log.info("Saved a new C2RActivity: {}", c2RActivity);
                eventBus.publish(this, new ActivitySyncEvent(ui.getUser(), c2RActivity));
                String locationsWithLineBreak = "<a href=\"" + newLocation + "\">" + newLocation + "</a>";
                new MNotification("Activity created at RunKeeper, you can find it at " + locationsWithLineBreak).withHtmlContentAllowed(true).withStyleName(NOTIFICATION_SUCCESS).withDelayMsec(10000).display();
            } catch (Exception e) {
                log.warn("Failed to create activity at RunKeeper", e);
                new MNotification("Failed to create the activity at RunKeeper: " + e.getMessage()).withStyleName(NOTIFICATION_ERROR).withDelayMsec(6000).display();
            }
        } else {
            log.debug("Already synced to RunKeeper, skip new sync");
            new MNotification("The activity was not sent since this activity was already synced. Use the 'Force' checkbox to force a re-sync").withDelayMsec(2500).withStyleName(NOTIFICATION_WARNING).display();
        }
        progressWindow.close();
        return newLocation != null;
    }


    //Create a request to record an activity
    private static RecordActivityRequest createRecordActivityRequest(Concept2CsvActivity csvActivity, InternalRunKeeperData internalRunKeeperData, ExternalRunkeeperData externalRunkeeperData) {
        Instant start;
        try {
            start = csvActivity.getDateAsInstant();
        } catch (ParseException e) {
            log.warn("Unable to parse the date from a Concept2CsvActivity into an instant. {}", csvActivity, e);
            start = Instant.now();
        }
        return RecordActivityRequest.builder()
                .equipment(Equipment.ROW_MACHINE)
                .notes(StringUtils.isEmpty(csvActivity.getComments()) ? internalRunKeeperData.getDefaultComment() : csvActivity.getComments())
                .postToFacebook(firstNonNull(internalRunKeeperData.getPostToFacebookOverride(), externalRunkeeperData.getSettings().isPostToFacebook()))
                .postToTwitter(firstNonNull(internalRunKeeperData.getPostToTwitterOverride(), externalRunkeeperData.getSettings().isPostToTwitter()))
                .startTime(start)
                .distance(csvActivity.getWorkDistance())
                .duration(csvActivity.getWorkTimeInSeconds())
                .totalCalories(csvActivity.getCalPerHours())
                .type(ActivityType.ROWING)
                .distances(csvActivity.getStrokeData().stream().map(sd -> RunKeeperDistance.builder().distance(sd.getDistance()).timestamp(sd.getSeconds()).build()).collect(toList()))
                .heartRates(csvActivity.getStrokeData().stream().filter(sd -> sd.getHeartRate() > 0).map(sd -> RunKeeperHeartRate.builder().heartRate((int) sd.getHeartRate()).timestamp(sd.getSeconds()).build()).collect(toList()))
                .build();
    }
}
