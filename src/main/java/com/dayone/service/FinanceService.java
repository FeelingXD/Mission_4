package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {


    private  final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    public ScrapedResult getDividendByCompanyName(String companyName){

        // 회사 명 기준 정보조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));
        // 배당금 조회
        System.out.println(company.getName());
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());
        // 반환

        List<Dividend> dividends = dividendEntities.stream()
                .map(e-> Dividend.builder()
                        .date(e.getDate())
                        .dividend(e.getDividend())
                        .build())
                .collect(Collectors.toList());

        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build(),dividends);
    }

}
