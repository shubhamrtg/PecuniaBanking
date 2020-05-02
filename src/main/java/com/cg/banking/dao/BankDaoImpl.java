package com.cg.banking.dao;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.cg.banking.entity.AccTransaction;
import com.cg.banking.entity.Account;
import com.cg.banking.entity.Branch;
import com.cg.banking.entity.Customer;
import com.cg.banking.entity.LoanRequest;

@Repository
public class BankDaoImpl implements BankDao{

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public boolean addCustomer(Customer cust) {
		em.persist(cust);
		return true;
	}

	@Override
	public boolean editCustomer(Customer cust) {
		em.merge(cust);
		return true;
	}

	@Override
	public Customer viewCustomer(String custId) {
		
		return em.find(Customer.class,custId);
	}

	@Override
	public boolean addAccount(Account account) {
		em.persist(account);
		return true;
	}

	@Override
	public boolean editAccount(Account account) {
		em.merge(account);
		return true;
	}

	@Override
	public Account viewAccount(String accId) {
		
		return em.find(Account.class, accId);
	}

	@Override
	public List<Account> viewAccounts(String customerId) {
		String jpql ="from Account a inner join fetch a.customer c where c.customerId=:custid";
		TypedQuery<Account> query = em.createQuery(jpql, Account.class);
		query.setParameter("custid", customerId);
		return query.getResultList();
		
	}

	@Override
	public boolean addTxn(AccTransaction tx) {
		em.persist(tx);
		return true;
	}

	@Override
	public List<AccTransaction> getTransactions(String accId, int no) {
		String jpql ="from AccTransaction tx inner join fetch tx.account a inner join fetch a.customer c where a.accountId=:accid order by tx.transaccountId desc";
		TypedQuery<AccTransaction> query = em.createQuery(jpql, AccTransaction.class);
		query.setParameter("accid", accId);
		query.setFirstResult(0);
		query.setMaxResults(no);
		return query.getResultList();
	}

	@Override
	public List<AccTransaction> getTransactions(String accId, LocalDate fromdt, LocalDate todt) {
		//String jpql ="from AccTransaction tx inner join fetch tx.account a inner join fetch a.customer c where a.accountId=:accid and tx.transDate=:from and tx.transDate=:to order by desc tx.transaccountId";
		String jpql ="from AccTransaction tx inner join fetch tx.account a inner join fetch a.customer c where a.accountId=:accid and tx.transDate between :from and :to order by  tx.transaccountId desc";
		TypedQuery<AccTransaction> query = em.createQuery(jpql, AccTransaction.class);
		query.setParameter("accid", accId);
		query.setParameter("from", fromdt);
		query.setParameter("to", todt);
		return query.getResultList();
	}

	@Override
	public boolean loanRequest(LoanRequest req) {
		em.persist(req);
		return true;
	}
	@Override
	public LoanRequest getLoanRequestStatus(String loanReqId) {
		
		return em.find(LoanRequest.class, loanReqId);
	}

	@Override
	public List<LoanRequest> getLoanRequest(String customerId) {
		String jpql ="from LoanRequest lr inner join fetch lr.customer c where c.customerId=:custid";
		TypedQuery<LoanRequest> query = em.createQuery(jpql, LoanRequest.class);
		query.setParameter("custid", customerId);
		return query.getResultList();
	}

	@Override
	public Branch viewBranch(String branchCode) {
		
		return em.find(Branch.class, branchCode);
	}

	@Override
	public int countLoansForCustomer(String custID) {
		String jpql ="select count(loanRequestId) from LoanRequest r inner join fetch r.customer.customerId=:custId";
		TypedQuery<Integer> query = em.createQuery(jpql,Integer.class);
		query.setParameter("custId", custID);
		return query.getSingleResult();
	}

	@Override
	public List<LoanRequest> getLoanRequestByStatus(String reqStatus) {
		String jpql ="from LoanRequest lr inner join fetch lr.customer c where lr.reqStatus=:reqstatus";
		TypedQuery<LoanRequest> query = em.createQuery(jpql, LoanRequest.class);
		query.setParameter("reqstatus", reqStatus);
		return query.getResultList();
	}

	@Override
	public int checkExistsLoanForCustomer(String custId) {
		String jpql ="select count(accountId) from Account a inner join fetch a.customer c where c.customerId=:custId and a.accountName like :loan";
		TypedQuery<Integer> query = em.createQuery(jpql,Integer.class);
		query.setParameter("custId", custId);
		query.setParameter("loan", "%loan%");
		return query.getSingleResult();
	}

	@Override
	public boolean editLoanRequest(LoanRequest req) {
		em.merge(req);
		return true;
	}

}







