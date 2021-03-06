package com.yazykov.currencyservice.service;

import com.yazykov.currencyservice.dto.BankCurrencyResponse;
import com.yazykov.currencyservice.dto.CurrencyResponse;
import com.yazykov.currencyservice.mappers.BankCurrencyResponseMapper;
import com.yazykov.currencyservice.mappers.CurrencyResponseMapper;
import com.yazykov.currencyservice.model.Currency;
import com.yazykov.currencyservice.repository.CurrencyRepository;
import com.yazykov.currencyservice.throwable.ConnectionToBankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final BankExchangeClient client;
    private final CurrencyRepository repository;
    private final CurrencyResponseMapper currencyResponseMapper;
    private final BankCurrencyResponseMapper bankCurrencyResponseMapper;

    public CurrencyResponse getLatestCurrency(){
        log.info("into currencyService method getLatestCurrency");

        Currency latestCurrency = repository.findFirstByOrderByCheckedAtDesc();
        return currencyResponseMapper.currencyToCurrencyResponse(latestCurrency);
    }

    @Scheduled(fixedRate = 30000000)
    protected void setCheckTimeAndSaveData() throws ConnectionToBankException {
        log.info("into scheduled method setCheckTimeAndSaveData");

        BankCurrencyResponse response = client.getCurrencyFromBank();

        if (response == null){
            log.error("Some problem with connection to bank");
            throw new ConnectionToBankException("Some problem with connection to bank");
        }

        Currency currency = bankCurrencyResponseMapper.bankCurrencyResponseToCurrency(response);
        currency = repository.save(currency);

        log.info("Currency with id: {} has been saved successfully",currency.getId());
    }
}
