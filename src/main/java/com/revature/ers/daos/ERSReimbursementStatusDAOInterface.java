package com.revature.ers.daos;

import com.revature.ers.models.ERSReimbursementStatus;

public interface ERSReimbursementStatusDAOInterface {

	ERSReimbursementStatus getReimbursementStatusById(int reimb_status_id);

	ERSReimbursementStatus getReimbursementStatusByStatus(String reimb_status);

	ERSReimbursementStatus createReimbursementStatus(ERSReimbursementStatus newReimbursementStatus);

}
