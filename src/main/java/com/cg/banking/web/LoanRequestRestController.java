package com.cg.banking.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cg.banking.exceptions.AccountException;
import com.cg.banking.exceptions.CustomerException;
import com.cg.banking.exceptions.LoanRequestException;
import com.cg.banking.service.LoanRequestService;
import com.cg.banking.util.AccountConstants;
import com.cg.banking.dto.AccountMessage;
import com.cg.banking.dto.LoanRequestForm;
import com.cg.banking.entity.LoanRequest;

@RestController
public class LoanRequestRestController {

	@Autowired
	public LoanRequestService service;
	
	@PostMapping("/addloanrequest")
	public AccountMessage addLoanRequest(@RequestBody LoanRequestForm reqForm) throws LoanRequestException, CustomerException {
		try {
			String loanReqId= service.loanRequest(reqForm);
			 return new AccountMessage(AccountConstants.LOAN_REQ_CREATED+ AccountConstants.GENERATED_LOAN_REQ+loanReqId);
		}catch(DataIntegrityViolationException ex) {
			throw new LoanRequestException(AccountConstants.ID_ALREADY_EXISTS);
		}
	}
	
	
	@GetMapping("/viewloanrequeststatus/{loanreqid}")
	public LoanRequest viewLoanRequestStatus(@PathVariable("loanreqid") String loanReqId)throws LoanRequestException{
		LoanRequest req=service.getLoanRequestStatus(loanReqId);
		if(req==null)
			throw new LoanRequestException("Loan Request Not Found");
		return req;
	}
	
	@GetMapping("/viewloanrequestsbycustid/{custid}")
	public List<LoanRequest> viewLoanRequestByCustomer(@PathVariable("custid") String custId) throws AccountException  {
		return service.getLoanRequest(custId);
	}
	
	@GetMapping("/viewnewloanrequests")
	public List<LoanRequest> viewNewLoanRequest() throws AccountException  {
		return service.getLoanRequestNew();
	}
	@GetMapping("/processLoan/{loanreqid}")
	public AccountMessage processLoan(@PathVariable("loanreqid") String loanReqId) throws AccountException, CustomerException  {
		service.processLoan(loanReqId);
		AccountMessage msg = new AccountMessage(AccountConstants.LOAN_PROCESSED);
		return msg;
	}
}





