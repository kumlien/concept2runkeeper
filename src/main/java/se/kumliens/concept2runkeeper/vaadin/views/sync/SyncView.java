package se.kumliens.concept2runkeeper.vaadin.views.sync;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Not;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.ui.MNotification;
import se.kumliens.concept2runkeeper.concept2.CsvActivity;
import se.kumliens.concept2runkeeper.runkeeper.ActivityType;
import se.kumliens.concept2runkeeper.runkeeper.Equipment;
import se.kumliens.concept2runkeeper.runkeeper.RecordActivityRequest;
import se.kumliens.concept2runkeeper.runkeeper.RunkeeperService;
import se.kumliens.concept2runkeeper.vaadin.MainUI;

import javax.annotation.PostConstruct;

import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;

import static com.vaadin.server.FontAwesome.EXCLAMATION_TRIANGLE;
import static com.vaadin.server.FontAwesome.LONG_ARROW_RIGHT;
import static com.vaadin.shared.ui.label.ContentMode.HTML;
import static com.vaadin.ui.Alignment.*;
import static com.vaadin.ui.Grid.SelectionMode.MULTI;
import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;
import static com.vaadin.ui.themes.ValoTheme.*;
import static se.kumliens.concept2runkeeper.domain.Provider.CONCEPT2;

/**
 * The view used by a user to sync work outs.
 * <p>
 * Created by svante2 on 2016-11-30.
 */
@SpringView
@RequiredArgsConstructor
@Slf4j
public class SyncView extends MVerticalLayout implements View {

    private final MainUI ui;

    private final RunkeeperService runkeeperService;

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
        Header header = new Header("Synchronize to: RunKeeper").setHeaderLevel(4);

        MVerticalLayout layout = new MVerticalLayout();
        layout.add(header, TOP_CENTER);
        return layout;
    }


    //Create the content representing where we synchronize from. Hardwire to concept2 for now
    private MVerticalLayout createFromContent() {
        Header header = new Header("Synchronize from: Concept2").setHeaderLevel(4);
        header.setSizeUndefined();

        MVerticalLayout layout = new MVerticalLayout();
        MHorizontalLayout topLayout = new MHorizontalLayout(header).withSpacing(true).withFullWidth();
        topLayout.withAlign(header, MIDDLE_LEFT);
        layout.add(topLayout);
        if (ui.getUser().hasConnectionTo(CONCEPT2)) {
            layout.add(new Label("Should not happen until we have set up connection with Concept2..."));
        } else {
            addC2WarningText(topLayout);
            MButton syncButton = new MButton("Send to RunKeeper").withIcon(LONG_ARROW_RIGHT).withStyleName("friendly");
            syncButton.setSizeUndefined();
            syncButton.setEnabled(false);


            topLayout.add(syncButton);
            topLayout.withAlign(syncButton, Alignment.MIDDLE_RIGHT);

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

                for(Iterator iter = selectedRows.iterator(); iter.hasNext();) {
                    CsvActivity next = (CsvActivity) iter.next();
                    Instant start = null;
                    try {
                        start = next.getDateAsInstant();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return;
                    }
                    RecordActivityRequest request = RecordActivityRequest.builder()
                            .equipment(Equipment.ROW_MACHINE)
                            .notes("Synchronized from Concept2")
                            .postToFacebook(false)
                            .postToTwitter(false)
                            .startTime(start)
                            .duration(Double.valueOf(next.getWorkTimeInSeconds()).intValue())
                            .totalCalories(next.getCalPerHours())
                            .type(ActivityType.ROWING).build();
                    URI location = runkeeperService.recordActivity(request, ui.getUser().getRunKeeperData().getToken());
                    Notification.show("Activity synchronized with RunKeeper: " + location.toString());
                    iter.remove();
                }

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
            grid.setRows(activities);
            Notification.show(activities.size() + " activities found");
        }, e -> {
            log.warn("Exception when uploading file", e);
            Notification.show("Unable to upload this file (" + e.getMessage() + ")");
        });
        dropBox.setSizeUndefined();
        return dropBox;
    }

    private void addC2WarningText(MHorizontalLayout top) {
        Button button = new MButton(EXCLAMATION_TRIANGLE).withStyleName(BUTTON_DANGER);

        String msg = "Since you don't have a connection set up with Concept2 yet we can't fetch your data automatically.</br>" +
                " You can still download your log file from Concept2 " + link("here", "http://log.concept2.com/history") + " and drop the file on the area below.";
        MNotification notification = new MNotification(msg, WARNING_MESSAGE).withHtmlContentAllowed(true).withDelayMsec(10000);
        notification.setIcon(EXCLAMATION_TRIANGLE);
        button.addClickListener(clk -> notification.show(Page.getCurrent()));
        top.add(button);
        top.withAlign(button, Alignment.MIDDLE_LEFT);
    }

    private static String link(String linkText, String location) {
        return new StringBuilder("<a href=\"").append(location).append("\">").append(linkText).append("</a>").toString();
    }


}
