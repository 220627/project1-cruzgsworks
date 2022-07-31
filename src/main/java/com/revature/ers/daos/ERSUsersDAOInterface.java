package com.revature.ers.daos;

import com.revature.ers.models.ERSUsers;

public interface ERSUsersDAOInterface {

	ERSUsers createUser(ERSUsers newUser);

	ERSUsers getUserByUserName(String ers_username);

	ERSUsers updateUser(ERSUsers requestData);

	ERSUsers updatePassword(ERSUsers requestData);

	ERSUsers getUserByUserId(int ers_users_id);

}
