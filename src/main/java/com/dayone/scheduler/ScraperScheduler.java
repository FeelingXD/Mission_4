package com.dayone.scheduler;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

    @Scheduled(cron = "0/5 * * * * *")
    public void test(){
        System.out.println("now ->"+System.currentTimeMillis());
    }

    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() throws InterruptedException {
        log.info("scraping scheduler is started");
        //저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        for (CompanyEntity company : companies) {
            log.info("scraping scheduler is started -> " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()
                    .ticker(company.getTicker())
                    .name(company.getName())
                    .build());

            //
            scrapedResult.getDividends().stream()
                    .map(e->new DividendEntity(company.getId(),e))
                    .forEach(e->{
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(),e.getDate());
                        if(!exists){
                            this.dividendRepository.save(e);
                        }
                    });
            //
            Thread.sleep(3000) ;
        }

    }
}
