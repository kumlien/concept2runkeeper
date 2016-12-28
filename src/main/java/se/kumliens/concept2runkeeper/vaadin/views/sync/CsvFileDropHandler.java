package se.kumliens.concept2runkeeper.vaadin.views.sync;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import se.kumliens.concept2runkeeper.concept2.CsvActivity;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import static com.vaadin.ui.Notification.Type.ERROR_MESSAGE;
import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;
import static java.util.stream.Collectors.toList;

/**
 * Created by svante2 on 2016-12-10.
 */
@Slf4j
class CsvFileDropHandler extends DragAndDropWrapper implements DropHandler {

    private static final long FILE_SIZE_LIMIT = 10 * 1024 * 1024; // 10MB

    private final SuccessListener successListener;

    private final ErrorListener errorListener;

    public CsvFileDropHandler(Component wrappedComponent, SuccessListener successListener, ErrorListener errorListener) {
        super(wrappedComponent);
        setDropHandler(this);
        this.successListener = successListener;
        this.errorListener = errorListener;
    }

    @Override
    public void drop(DragAndDropEvent dropEvent) {
        // expecting this to be an html5 drag
        final WrapperTransferable tr = (WrapperTransferable) dropEvent.getTransferable();
        final Html5File[] files = tr.getFiles();
        if (files == null || files.length != 1) {
            errorListener.onError(new IllegalArgumentException("The number of files dropped must be exactly one"));
        }

        Html5File file = files[0];
        if (file.getFileSize() > FILE_SIZE_LIMIT) {
            errorListener.onError(new IllegalArgumentException("This file is too big (" + file.getFileSize() / 1024 / 1024 + " MB), the max size is 10 MB"));
            return;
        }
        if (!file.getType().contains("text/csv")) {
            errorListener.onError(new IllegalArgumentException("The file must be a csv-file (but is of type " + file.getType() + ")"));
            return;
        }

        final ByteArrayOutputStream bas = new ByteArrayOutputStream();
        final StreamVariable streamVariable = new StreamVariable() {

            @Override
            public OutputStream getOutputStream() {
                return bas;
            }

            @Override
            public boolean listenProgress() {
                return true;
            }

            @Override
            public void onProgress(StreamingProgressEvent event) {
                log.info("{} out of {} bytes received", event.getBytesReceived(), event.getContentLength());
            }

            @Override
            public void streamingStarted(StreamingStartEvent event) {
                log.info("Streaming started: {}", event);
            }

            @Override
            public void streamingFinished(StreamingEndEvent event) {
                List<CsvActivity> activities;
                try {
                    activities = parseFile(bas).stream().filter(csvActivity -> StringUtils.hasText(csvActivity.getDate())).collect(toList());
                    successListener.onSuccess(activities);
                } catch (IOException e) {
                    errorListener.onError(e);
                }
            }

            @Override
            public void streamingFailed(StreamingErrorEvent event) {
                errorListener.onError(event.getException());
            }

            @Override
            public boolean isInterrupted() {
                return false;
            }
        };
        file.setStreamVariable(streamVariable);
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return AcceptAll.get();
    }

    private List<CsvActivity> parseFile(final ByteArrayOutputStream bas) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(CsvActivity.class).withUseHeader(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bas.toByteArray());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("utf8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        try {
            MappingIterator<CsvActivity> activities = mapper.readerFor(CsvActivity.class).with(schema).readValues(reader);
            return activities.readAll();
        } finally {
            bas.close();
        }
    }

    public interface StartListener {
        void start(StreamVariable.StreamingStartEvent evt);
    }

    public interface SuccessListener {
        void onSuccess(List<CsvActivity> activities);
    }

    public interface ErrorListener {
        void onError(Exception e);
    }

    public interface ProgressListener {
        void onProgress(StreamVariable.StreamingProgressEvent event);
    }
}
