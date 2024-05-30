package com.libraryapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.libraryapi.model.entity.Loan;

@Service
public class ScheduleService {
	
	private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";
	
	private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
	
    @Value("${lateloans.message}")
	private String message;
	
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans(){
    	log.info("Iniciando scheduled...");
        List<Loan> allLateLoans = loanService.getAllLateLoans();
        List<String> mailsList = allLateLoans.stream()
                .map(loan -> loan.getCustomerEmail())
                .collect(Collectors.toList());

        emailService.sendMails(message, mailsList);
    }

}
