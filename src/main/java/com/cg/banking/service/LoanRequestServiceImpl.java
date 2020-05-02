package com.cg.banking.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cg.banking.dao.BankDao;
import com.cg.banking.dto.AccountForm;
import com.cg.banking.dto.LoanRequestForm;
import com.cg.banking.entity.Customer;
import com.cg.banking.entity.LoanRequest;
import com.cg.banking.exceptions.AccountException;
import com.cg.banking.exceptions.CustomerException;
import com.cg.banking.exceptions.LoanRequestException;
import com.cg.banking.util.AccountConstants;

@Service("loanreqser")
@Transactional
public class LoanRequestServiceImpl implements LoanRequestService {

	@Autowired
	private BankDao dao;
	
	@Autowired
	private AccountService accountService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public String loanRequest(LoanRequestForm req) throws CustomerException {
		Customer cust = dao.viewCustomer(req.getCustomerId());
		if (cust == null)
			throw new CustomerException(AccountConstants.INVALID_CUSTOMER);
		String loanReqId = req.getCustomerId()+AccountConstants.EMPTY+(dao.countLoansForCustomer(req.getCustomerId())+1);
		LoanRequest loanReq = new LoanRequest();
		loanReq.setLoanRequestId(loanReqId);
		loanReq.setLoanAmount(req.getLoanAmt());
		loanReq.setLoanTenure(req.getTenure());
		loanReq.setLoanType(req.getLoanType());
		loanReq.setCustomer(cust);
		loanReq.setReqStatus(AccountConstants.LOAN_REQUEST);
		loanReq.setDateOfRequest(LocalDate.now());
		loanReq.setAnnualIncome(req.getAnnualIncome());
		dao.loanRequest(loanReq);
		return loanReqId;
	}

	@Override
	public LoanRequest getLoanRequestStatus(String loanReqId) {
		return dao.getLoanRequestStatus(loanReqId);
	}

	@Override
	public List<LoanRequest> getLoanRequest(String customerId) throws AccountException {
		List<LoanRequest> loanReqList =  dao.getLoanRequest(customerId);
		if(loanReqList.isEmpty())
			throw new AccountException(AccountConstants.NO_LOAN);
		loanReqList.sort((l1, l2)->l2.getDateOfRequest().compareTo(l1.getDateOfRequest()));
		return loanReqList;
	}

	@Override
	public List<LoanRequest> getLoanRequestNew() throws AccountException {
		List<LoanRequest> loanReqList = dao.getLoanRequestByStatus(AccountConstants.LOAN_REQUEST);
		if(loanReqList.isEmpty())
			throw new AccountException(AccountConstants.NO_LOAN);
		loanReqList.sort((l1, l2)->l1.getDateOfRequest().compareTo(l2.getDateOfRequest()));
		return loanReqList;
		
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public boolean processLoan(String loanReqId) throws CustomerException {
		LoanRequest req = dao.getLoanRequestStatus(loanReqId);
		Customer cust = req.getCustomer();
		if (dao.checkExistsLoanForCustomer(cust.getCustomerId()) <= AccountConstants.LOAN_EXISTS) {
			req.setReqStatus(AccountConstants.LOAN_REJECTED);
			dao.editLoanRequest(req);
	        return false;
		}
		double compoundInt = calcCompoundInt(req.getLoanAmount(), req.getLoanTenure());
		double emi = compoundInt/ (req.getLoanTenure() * AccountConstants.TWELVE_MONTHS);
		if (emi > (req.getAnnualIncome()/ AccountConstants.TWELVE_MONTHS)*AccountConstants.FIFTY_PERCENT) {
			req.setReqStatus(AccountConstants.LOAN_REJECTED);
			dao.editLoanRequest(req);
			return false;
		}
		req.setReqStatus(AccountConstants.LOAN_APPROVED);
		dao.editLoanRequest(req);
		AccountForm form = new AccountForm();
		form.setAccountName(req.getLoanType()+AccountConstants.LOAN);
		form.setBalance(req.getLoanAmount());
		form.setCustomerID(req.getCustomer().getCustomerId());
		
		accountService.addAccount(form);
		return true;
	}

	public double calcCompoundInt(double loanAmt, int tenure) {
		return loanAmt  * Math.pow((AccountConstants.ONE+ AccountConstants.LOAN_RATE), tenure);
	}
}




