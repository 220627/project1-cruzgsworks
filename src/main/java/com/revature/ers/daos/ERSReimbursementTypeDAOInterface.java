package com.revature.ers.daos;

import com.revature.ers.models.ERSReimbursementType;

public interface ERSReimbursementTypeDAOInterface {

	ERSReimbursementType getReimbursementTypeById(int reimb_type_id);

	ERSReimbursementType getReimbursementTypeByReimbType(String reimb_type);

	ERSReimbursementType createReimbursementType(ERSReimbursementType newReimbursementType);

}
