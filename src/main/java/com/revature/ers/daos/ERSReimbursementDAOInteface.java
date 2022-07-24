package com.revature.ers.daos;

import java.util.ArrayList;

import com.revature.ers.models.ERSReimbursement;

public interface ERSReimbursementDAOInteface {

	ERSReimbursement addNewRequest(ERSReimbursement myReimb);

	ArrayList<ERSReimbursement> getReimbursementRequest(int reimb_status_id, int reimb_author);

	ERSReimbursement getReceipt(int reimb_id, int ers_users_id);

	ArrayList<ERSReimbursement> getAllReimbursementRequests(int reimb_status_id);

	ERSReimbursement resolveReimbursements(int reimb_id, int ers_users_id, int reimb_status_id);

	ArrayList<ERSReimbursement> getAllReimbursementRequestsPagination(int reimb_status_id, int limit, int offset);

	ArrayList<ERSReimbursement> getReimbursementRequestPagination(int reimb_status_id, int reimb_author, boolean isManager, int limit,
			int page);

	int countReimbursements(boolean isManager, int author_id, int reimb_status);

}
