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
public class ErrorDto<T> {
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());
	private T message;
	private String path;
}
