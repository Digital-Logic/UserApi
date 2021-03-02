package net.digitallogic.ProjectManager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
@Profile(Profiles.NOT_NON_ASYNC)
public class AsyncConfig implements AsyncConfigurer {

}
