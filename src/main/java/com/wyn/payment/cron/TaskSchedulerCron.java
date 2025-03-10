package com.wyn.payment.cron;


import com.wyn.payment.service.CardDetailService;
import com.wyn.payment.util.Gentools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class TaskSchedulerCron {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSchedulerCron.class);

    @Autowired
    private CardDetailService cardDetailService;

    @Value("${EXPIRE_DAYS}")
    private String expireDays;

    @Value("${EXPIRE_DAYS_SPLIT}")
    private String expireDaysSplit;

    // 1 Seconds 2 Minutes 3 Hours 4 Day-of-Month 5 Month 6 Day-of-Week 7 Year (optional field)
    @Scheduled(cron="0 0 0 * * *")
    public void expireOldCardsbyDays() {
        LOGGER.info("***************************CRON : expireOldCardsbyDays : " + Calendar.getInstance().getTime() + "**********************************");
        cardDetailService.expireOldCardsbyDays(Gentools.parseInt(expireDays), Gentools.parseInt(expireDaysSplit));
        LOGGER.info("***********************************************************************************************************************");
    }
}
