package zielu.intellij.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.util.text.SyncDateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import zielu.TestType;

@Tag(TestType.FAST)
class ZDateFormatUtilTest {
  private static final long MINUTE_MILLIS = 60 * 1000;
  private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
  private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
  private static final SyncDateFormat DATE_FORMAT = new SyncDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));

  private Date now = new Date(Clock.fixed(Instant.parse("2007-12-03T10:15:30.00Z"), ZoneOffset.UTC).millis());

  @Test
  void formatMomentsAgo() {
    Date date = new Date(now.getTime() - 1);
    String text = ZDateFormatUtil.formatPrettyDateTime(date, now, DATE_FORMAT);
    assertThat(text).isEqualTo("Moments ago");
  }

  @ParameterizedTest
  @CsvSource({
      MINUTE_MILLIS + ",A minute ago",
      (MINUTE_MILLIS * 2) + ",2 minutes ago",
      (MINUTE_MILLIS * 60) + ",1 hour ago"
  })
  void formatMinutesAgo(long diffFromNow, String expectedText) {
    Date date = new Date(now.getTime() - diffFromNow);
    String text = ZDateFormatUtil.formatPrettyDateTime(date, now, DATE_FORMAT);
    assertThat(text).isEqualTo(expectedText);
  }

  @ParameterizedTest
  @CsvSource({
      HOUR_MILLIS + ",1 hour ago",
      (HOUR_MILLIS + MINUTE_MILLIS) + ",Today"
  })
  void formatHoursAgo(long diffFromNow, String expectedText) {
    Date date = new Date(now.getTime() - diffFromNow);
    String text = ZDateFormatUtil.formatPrettyDateTime(date, now, DATE_FORMAT);
    assertThat(text).isEqualTo(expectedText);
  }

  @ParameterizedTest
  @CsvSource({
      (HOUR_MILLIS + MINUTE_MILLIS) + ",Today",
      (HOUR_MILLIS + MINUTE_MILLIS * 2) + ",Today",
      (HOUR_MILLIS + MINUTE_MILLIS * 60) + ",Today",
      (HOUR_MILLIS + MINUTE_MILLIS * 110) + ",Today",
      (HOUR_MILLIS + MINUTE_MILLIS * 60 * 23) + ",Today",
      (HOUR_MILLIS + MINUTE_MILLIS * 60 * 23) + MINUTE_MILLIS + ",Yesterday"
  })
  void formatToday(long diffFromNow, String expectedText) {
    Date date = new Date(now.getTime() - diffFromNow);
    String text = ZDateFormatUtil.formatPrettyDateTime(date, now, DATE_FORMAT);
    assertThat(text).isEqualTo(expectedText);
  }

  @ParameterizedTest
  @CsvSource({
      (DAY_MILLIS + MINUTE_MILLIS) + ",Yesterday",
      (DAY_MILLIS + HOUR_MILLIS) + ",Yesterday",
      (DAY_MILLIS + HOUR_MILLIS * 24) + ",Yesterday"
  })
  void formatYesterday(long diffFromNow, String expectedText) {
    Date date = new Date(now.getTime() - diffFromNow);
    String text = ZDateFormatUtil.formatPrettyDateTime(date, now, DATE_FORMAT);
    assertThat(text).isEqualTo(expectedText);
  }

  @Test
  void formatAsAbsoluteDateTime() {
    Date date = new Date(now.getTime() - (DAY_MILLIS * 2 + HOUR_MILLIS));
    String text = ZDateFormatUtil.formatPrettyDateTime(date, now, DATE_FORMAT);
    assertThat(text).isEqualTo(DATE_FORMAT.format(date));
  }
}