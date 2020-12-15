package net.digitallogic.ProjectManager.persistence.biTemporal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Constants {
	public static final LocalDateTime MIN_DATE = LocalDateTime.of(
			LocalDate.of(-4713, 1, 1), LocalTime.MIN );

	public static final LocalDateTime MAX_DATE = LocalDateTime.of(
			LocalDate.of(294276, 12, 31), LocalTime.MIDNIGHT );
}
