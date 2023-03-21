package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) throws IOException {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists){
            throw new RuntimeException("already excist ticker :" +ticker );
        }

        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        return this.companyRepository.findAll(pageable);
    }
    private Company storeCompanyAndDividend(String ticker) throws IOException {
        //ticker 로 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)){
            throw new RuntimeException("fail to scrap ticker -> "+ticker);
        }

        //있는경우 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        //결과 저장
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntityList);


        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword){
        Pageable limit= PageRequest.of(0,10);
        Page<CompanyEntity> companyEntities= this.companyRepository.findByNameStartingWithIgnoreCase(keyword,limit);
        return companyEntities.stream().map(e->e.getName()).collect(Collectors.toList());
    }

    public void addAutocompleteKeyword(String keyword){
        this.trie.put(keyword,null);
    }

    public List<String> autocomplete(String keyword){
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword){
        this.trie.remove(keyword);
    }
}
