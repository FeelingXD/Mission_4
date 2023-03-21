package com.dayone.scraper;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;

import java.io.IOException;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker) throws IOException;
    ScrapedResult scrap(Company company);
}
