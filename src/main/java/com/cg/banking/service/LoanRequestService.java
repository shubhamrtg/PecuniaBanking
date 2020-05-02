package com.cg.banking.service;

import java.util.List;

import com.cg.banking.dto.LoanRequestForm;
import com.cg.banking.entity.LoanRequest;
import com.cg.banking.exceptions.AccountException;
import com.cg.banking.exceptions.CustomerException;

public interface LoanRequestService {
	
	public String loanRequest(LoanRequestForm req)  throws CustomerException;
	public LoanRequest getLoanRequestStatus(String loanReqId);
	public List<LoanRequest> getLoanRequest(String customerId)throws AccountException;
	public List<LoanRequest> getLoanRequestNew()throws AccountException;
	public boolean processLoan(String loanReqId)throws CustomerException ;

}
