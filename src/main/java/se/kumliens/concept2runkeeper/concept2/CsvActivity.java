package se.kumliens.concept2runkeeper.concept2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import se.kumliens.concept2runkeeper.domain.ExternalActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Created by svante2 on 2016-12-09.
 */
@Data
@ToString
@EqualsAndHashCode(of = {"date"})
@JsonPropertyOrder({ "Date",
        "Description",
        "Work Time (Formatted)",
        "Work Time (Seconds)","Rest Time (Formatted)","Rest Time (Seconds)","Work Distance","Rest Distance","Stroke Rate","Pace","Avg Watts","Cal/Hour","Avg Heart Rate","Age","Weight","Type","Ranked","Comments" })
public class CsvActivity implements ExternalActivity {

    public static final String FIELD_DATE = "date";
    public static final String FIELD_WORK_DISTANCE = "distance";
    public static final String FIELD_WORK_TIME_IN_SECONDS = "workTimeInSeconds";
    public static final String FIELD_PACE = "pace";
    public static final String FIELD_TYPE = "type";

    @JsonProperty("Date")
    private String date;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Work Time (Formatted)")
    private String formattedWorkTime;

    @JsonProperty("Work Time (Seconds)")
    private Double workTimeInSeconds;

    @JsonProperty("Rest Time (Formatted)")
    private String formattedRestTime;

    @JsonProperty("Rest Time (Seconds)")
    private String restTimeSeconds;

    @JsonProperty("Work Distance")
    private String workDistance;

    @JsonProperty("Rest Distance")
    private String restDistance;

    @JsonProperty("Stroke Rate")
    private String strokeRate;

    @JsonProperty("Avg Watts")
    private String avgWatts;

    @JsonProperty("Avg Heart Rate")
    private String avgHeartRate;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Pace")
    private String pace;

    @JsonProperty("Cal/Hour")
    private Double calPerHour;

    @JsonProperty("Age")
    private String age;

    @JsonProperty("Weight")
    private String weigth;

    @JsonProperty("Ranked")
    private String ranked;

    @JsonProperty("Comments")
    private String comments;

    public Instant getDateAsInstant() throws ParseException {
        if(date == null) return null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = dateFormat.parse(date);
        return Instant.ofEpochMilli(parse.getTime());
    }

    public Double getCalPerHours() {
        return workTimeInSeconds / 3600 * calPerHour;
    }


}
