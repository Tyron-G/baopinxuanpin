package com.oneaix.selection.monitoring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 2026-06-04 系统运行元信息 */
@Component
public class SelectionInfoContributor implements InfoContributor {

    private final Environment environment;
    private final String version;
    private final String releaseDate;
    private final String sampleDataVersion;

    public SelectionInfoContributor(
            Environment environment,
            @Value("${selection.metadata.version}") String version,
            @Value("${selection.metadata.release-date}") String releaseDate,
            @Value("${selection.metadata.sample-data-version}") String sampleDataVersion
    ) {
        this.environment = environment;
        this.version = version;
        this.releaseDate = releaseDate;
        this.sampleDataVersion = sampleDataVersion;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("application", Map.of(
                "name", environment.getProperty("spring.application.name", "selection-service"),
                "version", version,
                "releaseDate", releaseDate
        ));
        builder.withDetail("runtime", Map.of(
                "port", environment.getProperty("server.port", "8080"),
                "timeZone", "Asia/Shanghai"
        ));
        builder.withDetail("data", Map.of(
                "store", "H2 in-memory",
                "seedProfile", "builtin-demo",
                "sampleDataVersion", sampleDataVersion
        ));
    }
}
