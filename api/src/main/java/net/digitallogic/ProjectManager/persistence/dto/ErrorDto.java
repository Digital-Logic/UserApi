package net.digitallogic.ProjectManager.persistence.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());
	private Object message;
	private Object details;
	private String path;
}
