package se.kumliens.concept2runkeeper.vaadin.views.sync;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.VaadinSessionScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.ClickableRenderer;
import com.vaadin.v7.ui.renderers.ImageRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MPanel;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;
import org.vaadin.viritin.ui.MNotification;
import org.vaadin.viritin.v7.fields.MCheckBox;
import org.vaadin.viritin.v7.label.MLabel;
import se.kumliens.concept2runkeeper.domain.concept2.Concept2CsvActivity;
import se.kumliens.concept2runkeeper.domain.concept2.Concept2CsvStrokeData;
import se.kumliens.concept2runkeeper.domain.runkeeper.RunkeeperActivity;
import se.kumliens.concept2runkeeper.vaadin.MainUI;
import se.kumliens.concept2runkeeper.vaadin.converters.Concept2DistanceConverter;
import se.kumliens.concept2runkeeper.vaadin.converters.Concept2WorkTimeConverter;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;

import static com.vaadin.server.FontAwesome.*;
import static com.vaadin.ui.themes.ValoTheme.*;
import static java.util.stream.Collectors.toList;
import static se.kumliens.concept2runkeeper.domain.Provider.RUNKEEPER;
import static se.kumliens.concept2runkeeper.vaadin.C2RThemeResources.HEART_RATE;

/**
 * Created by svante2 on 2017-04-17.
 */
@SpringComponent
@VaadinSessionScope
@Slf4j
@RequiredArgsConstructor
public class Concept2Panel extends Panel {

    private final MainUI ui;

    void setSynchronizeView(SynchronizeView synchronizeView) {
        this.synchronizeView = synchronizeView;
    }

    private SynchronizeView synchronizeView;

    private MCheckBox forceSync;

    private BeanItemContainer<Concept2CsvActivity> concept2Container = new BeanItemContainer<>(Concept2CsvActivity.class);

    //Create the content representing where we synchronize from. Hard wire to concept2 for now
    @PostConstruct
    public void createContent() {
        MVerticalLayout root = new MVerticalLayout();

        //Create the tab sheet
        TabSheet concept2TabSheet = new TabSheet();
        concept2TabSheet.addStyleName(TABSHEET_FRAMED);
        concept2TabSheet.addStyleName(TABSHEET_PADDED_TABBAR);

        //Add the api tab
        concept2TabSheet.addTab(new MVerticalLayout(new Label("Sorry, currently we have no access to the Concept2 API, please use the file based import for now.")), "API-Based import", CHAIN);

        //Add the csv tab
        MVerticalLayout csvTabLayout = new MVerticalLayout().withHeightUndefined();
        concept2TabSheet.addTab(csvTabLayout, "CSV-File import", FILE_TEXT_O);

        MButton syncButton = new MButton("Send to RunKeeper").withIcon(LONG_ARROW_RIGHT).withStyleName("friendly");
        syncButton.setEnabled(ui.getUser().hasConnectionTo(RUNKEEPER));
        syncButton.setSizeUndefined();
        syncButton.setEnabled(false);

        MButton howToBtn = new MButton().withStyleName(BUTTON_BORDERLESS).withIcon(QUESTION_CIRCLE);
        howToBtn.addClickListener(evt ->
                new MNotification(
                        "Here you can sync your Concept2 activities using the file export functionality from Concept2.<br>" +
                                "<ol>" +
                                "<li>Export your activities as a .csv file from the Concept2 <a href=\"http://log.concept2.com/history\">history tab</a></li>" +
                                "<li>Drop the file on the table below, select some activities and push the green button.</li>" +
                                "<li>Any activities not already synced with RunKeeper will be sent to RunKeeper.</li>" +
                                "<li>Use the check-box to the right if you want to force sync of already synced activities.</li></ol>")
                        .withDelayMsec(15000).withStyleName(NOTIFICATION_SMALL).withStyleName(NOTIFICATION_CLOSABLE).withHtmlContentAllowed(true).display());

        forceSync = new MCheckBox("Force sync of activities already synced").withValueChangeListener(evt -> {
            if (((CheckBox) evt.getProperty()).getValue()) {
                new MNotification("We try to make sure a given activity in only synced once.<br>" +
                        "By using 'Force' you bypass this check and the activity will be synced anyway.<br>" +
                        "This might lead to duplicate activities at your RunKeeper account.<br>" +
                        "Just saying...").withDelayMsec(7500).withHtmlContentAllowed(true).withStyleName(ValoTheme.NOTIFICATION_WARNING).display();
            }
        });

        csvTabLayout.add(new MHorizontalLayout(howToBtn, syncButton, forceSync).withAlign(forceSync, Alignment.MIDDLE_LEFT).withSpacing(true));

        GeneratedPropertyContainer generatedPropertyContainer = new GeneratedPropertyContainer(concept2Container);

        //Add a generated column for the heart rate
        generatedPropertyContainer.addGeneratedProperty(RunkeeperActivity.HEART_RATE, new PropertyValueGenerator<Number[]>() {
            @Override
            public Number[] getValue(Item item, Object itemId, Object propertyId) {
                Concept2CsvActivity activity = ((BeanItem<Concept2CsvActivity>) item).getBean();
                List<Double> heartRates = activity.getStrokeData().stream().map(Concept2CsvStrokeData::getHeartRate).collect(toList());
                return heartRates.toArray(new Double[]{});
            }

            @Override
            public Class<Number[]> getType() {
                return Number[].class;
            }
        });

        //Add a generated column for the pace
        generatedPropertyContainer.addGeneratedProperty("Pace", new PropertyValueGenerator<Number[]>() {
            @Override
            public Number[] getValue(Item item, Object itemId, Object propertyId) {
                Concept2CsvActivity activity = ((BeanItem<Concept2CsvActivity>) item).getBean();
                List<Double> heartRates = activity.getStrokeData().stream().map(Concept2CsvStrokeData::getPace).collect(toList());
                return heartRates.toArray(new Double[]{});
            }

            @Override
            public Class<Number[]> getType() {
                return Number[].class;
            }
        });

        //Add a generated column with the button to add stroke data
        generatedPropertyContainer.addGeneratedProperty("addStrokeData", new PropertyValueGenerator<Resource>() {
            @Override
            public Resource getValue(Item item, Object itemId, Object propertyId) {
                return HEART_RATE;
            }

            @Override
            public Class<Resource> getType() {
                return Resource.class;
            }
        });


        com.vaadin.v7.ui.Grid grid = new com.vaadin.v7.ui.Grid(generatedPropertyContainer);
        //grid.setImmediate(true);
        grid.setSelectionMode(com.vaadin.v7.ui.Grid.SelectionMode.SINGLE);
        grid.setWidth("100%");
        grid.setHeight("100%");
        grid.setContainerDataSource(generatedPropertyContainer);
        grid.removeAllColumns();
        grid.addColumn("date").setHeaderCaption("Date");
        grid.addColumn("workDistance").setHeaderCaption("Distance").setConverter(new Concept2DistanceConverter());
        grid.addColumn("workTimeInSeconds").setHeaderCaption("Time").setConverter(new Concept2WorkTimeConverter()).setWidth(180);
        grid.addColumn("pace").setHeaderCaption("Average pace (s/500 m)").setWidth(200);
        grid.addColumn("type").setHeaderCaption("(Concept2-) Type");
        //grid.addColumn(RunkeeperActivity.HEART_RATE).setRenderer(new SparklineRenderer());
        //an grid.addColumn("Pace").setRenderer(new SparklineRenderer());
        grid.addColumn("addStrokeData").setWidth(140).setRenderer(new ImageRenderer((ClickableRenderer.RendererClickListener) e -> {
            Concept2CsvActivity concept2CsvActivity = (Concept2CsvActivity) e.getItemId();
            Upload upload = null;
            MWindow uploadWindow = new MWindow("Add stroke data");
            try {
                File tempFile = File.createTempFile("temp", ".csv");
                upload = new Upload("Choose the Concept2 result file matching this activity", (filename, mimetype) -> {
                    try {
                        return new FileOutputStream(tempFile);
                    } catch (IOException e1) {
                        log.warn("Unable to create local file for stroke data", e1);
                        new MNotification("Unable to create a local file: " + e1.getMessage()).withStyleName(NOTIFICATION_ERROR).display();
                        return null;
                    }
                });
                upload.addFinishedListener(finishedEvent -> {
                    try {
                        FileReader fileReader = new FileReader(tempFile);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        CsvMapper mapper = new CsvMapper();
                        CsvSchema csvSchema = mapper.schemaFor(Concept2CsvStrokeData.class).withUseHeader(true);
                        MappingIterator<Concept2CsvStrokeData> values = mapper.readerFor(Concept2CsvStrokeData.class).with(csvSchema).readValues(bufferedReader);
                        List<Concept2CsvStrokeData> strokeData = values.readAll();
                        double lastDistance = CollectionUtils.isEmpty(strokeData) ? 0.0 : strokeData.get(strokeData.size() - 1).getDistance();
                        if (Math.abs(lastDistance - concept2CsvActivity.getWorkDistance()) > 25) {
                            ConfirmDialog.show(getUI(), "Difference in distance",
                                    "The result file and the activity differ somewhat in distance (the activity is " + concept2CsvActivity.getWorkDistance().intValue() + " meters but the result file is " + (int) lastDistance + " meters), are you sure you want to use this file?",
                                    "Yes", "No",
                                    dialog -> {
                                        if (dialog.isConfirmed()) {
                                            afterAddedVerifiedStrokeData(grid, concept2CsvActivity, uploadWindow, strokeData);
                                        }
                                    });
                        } else {
                            afterAddedVerifiedStrokeData(grid, concept2CsvActivity, uploadWindow, strokeData);
                        }

                    } catch (Exception e1) {
                        log.warn("Error reading file which should contain concept2 stroke data", e1);
                        uploadWindow.close();
                        new MNotification("Error", "Unable to read that file: " + e1.getMessage()).display();
                    }
                });
                upload.setButtonCaption("Attach stroke data");
            } catch (IOException e1) {
                log.warn("Error when adding stroke data to activity", e1);
                new MNotification("An error occurred: " + e1.getMessage()).withStyleName(NOTIFICATION_ERROR).display();
            }
            uploadWindow.withContent(new MPanel(new MHorizontalLayout(upload).withMargin(true).withSpacing(true).space()))
                    .withModal(true)
                    .withResizable(false);

            ui.addWindow(uploadWindow);
        }));
        grid.addSelectionListener(evt -> {
            syncButton.setEnabled(!grid.getSelectedRows().isEmpty());
        });

        setUpSyncClickHandler(syncButton, grid);

        DragAndDropWrapper dropArea = getDropAreaWithGrid(grid);
        dropArea.setWidth("100%");
        csvTabLayout.expand(dropArea);

        root.expand(concept2TabSheet);
        setContent(root);
        setCaption("Your Concept2 activities");
    }

    //Handle the actual sync process to RunKeeper
    private void setUpSyncClickHandler(MButton syncButton, Grid concept2Grid) {
        syncButton.addClickListener(evt -> {
            Object selectedRow = concept2Grid.getSelectedRow();
            if (selectedRow == null) {
                log.warn("Sync button clicked without selected row in the grid!");
                new MNotification("No selection detected (this is a bug). Try to re-select the activity").withStyleName(NOTIFICATION_ERROR).withDelayMsec(5000).display();
                return;
            }
            if (synchronizeView.doSync((Concept2CsvActivity) selectedRow, forceSync.isChecked())) {
                concept2Grid.getContainerDataSource().removeItem(selectedRow);
            }
            concept2Grid.getSelectionModel().reset();
            concept2Grid.clearSortOrder();
        });
    }


    private void afterAddedVerifiedStrokeData(com.vaadin.v7.ui.Grid grid, Concept2CsvActivity concept2CsvActivity, MWindow uploadwindow, List<Concept2CsvStrokeData> strokeData) {
        concept2CsvActivity.setStrokeData(strokeData);
        uploadwindow.close();
        grid.refreshRows(concept2CsvActivity);
        new MNotification("Stroke data added").withDelayMsec(1500).withStyleName(NOTIFICATION_SUCCESS).display();
    }

    private DragAndDropWrapper getDropAreaWithGrid(Grid grid) {
        final CsvFileDropHandler dropBox = new CsvFileDropHandler(grid, activities -> {
            if (!CollectionUtils.isEmpty(activities)) {
                grid.getContainerDataSource().removeAllItems();
                activities.forEach(grid.getContainerDataSource()::addItem);
                new MNotification(activities.size() + (activities.size() > 1 ? " activities found" : " activity found")).withDelayMsec(1000).withStyleName(NOTIFICATION_SUCCESS).display();
            } else {
                new MNotification("No activities found!").withDelayMsec(1000).withStyleName(NOTIFICATION_WARNING).display();
            }
        }, e -> {
            log.warn("Exception when uploading file: {}", e.getMessage());
            new MNotification("Sorry, but we are unable to parse this file (" + e.getMessage() + ")").withDelayMsec(5000).withStyleName(NOTIFICATION_FAILURE).display();
        });
        dropBox.setSizeUndefined();
        return dropBox;
    }
}
