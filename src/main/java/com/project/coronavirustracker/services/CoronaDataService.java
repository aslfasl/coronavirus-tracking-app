package com.project.coronavirustracker.services;

import com.project.coronavirustracker.models.AreaStats;
import lombok.Getter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
public class CoronaDataService {

    private static String VIRUS_REPORTS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    // Saving state in a service is a mistake. I just was not in a mood to add a DB connection.
    private List<AreaStats> allStats = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void getVirusData() throws IOException, InterruptedException {
        List<AreaStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_REPORTS_DATA_URL))
                .build();

        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        StringReader csvReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
                .parse(csvReader);
        for (CSVRecord record : records) {
            AreaStats areaStats = new AreaStats();
            areaStats.setProvince(record.get("Province/State").equals("") ? "None" : record.get("Province/State"));
            areaStats.setCountry(record.get("Country/Region"));
            int latestTotalCases = Integer.parseInt(record.get(record.size() - 1));
            int previousCases = Integer.parseInt(record.get(record.size() - 2));
            areaStats.setLatestTotalCases(latestTotalCases);
            areaStats.setTendency(latestTotalCases - previousCases);
            newStats.add(areaStats);
        }
        this.allStats = newStats;
    }
}
