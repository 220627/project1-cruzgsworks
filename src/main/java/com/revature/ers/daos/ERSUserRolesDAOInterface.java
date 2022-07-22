package com.revature.ers.daos;

import com.revature.ers.models.ERSUserRoles;

public interface ERSUserRolesDAOInterface {

	ERSUserRoles getRoleById(int esr_user_role_id);

	ERSUserRoles getRoleByUseRole(String user_role);

	ERSUserRoles createRole(ERSUserRoles newRole);

}
