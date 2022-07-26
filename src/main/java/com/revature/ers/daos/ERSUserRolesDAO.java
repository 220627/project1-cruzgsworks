package com.revature.ers.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.ers.models.ERSUserRoles;
import com.revature.ers.utils.ConnectionUtil;

public class ERSUserRolesDAO implements ERSUserRolesDAOInterface {
	
	public static Logger log = LogManager.getLogger();

	@Override
	public ERSUserRoles getRoleById(int esr_user_role_id) {
		String SQL = "SELECT * FROM ers.ers_user_roles WHERE ers_user_role_id = ?";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);

			ps.setInt(1, esr_user_role_id);

			ResultSet getRole = ps.executeQuery();

			if (getRole.next()) {
				ERSUserRoles getRoleObj = new ERSUserRoles(
						getRole.getInt(1),
						getRole.getString(2));
				log.info("Retrieved role by ID - " + getRoleObj.getUser_role());
				return getRoleObj;
			}
		} catch (SQLException ex) {
			log.error(ex.getMessage());

		} catch (Exception ex) {
			log.error(ex.getMessage());

		}
		return null;
	}

	@Override
	public ERSUserRoles getRoleByUseRole(String user_role) {
		String SQL = "SELECT * FROM ers.ers_user_roles WHERE user_role = ?";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);

			ps.setString(1, user_role);

			ResultSet getRole = ps.executeQuery();

			if (getRole.next()) {
				ERSUserRoles getRoleObj = new ERSUserRoles(
						getRole.getInt(1),
						getRole.getString(2));
				log.info("Retrieved role by name - " + getRoleObj.getUser_role());
				return getRoleObj;
			}
		} catch (SQLException ex) {
			log.error(ex.getMessage());

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return null;
	}

	@Override
	public ERSUserRoles createRole(ERSUserRoles newRole) {

		try (Connection conn = ConnectionUtil.getConnection()) {

			ERSUserRoles getUserRole = this.getRoleByUseRole(newRole.getUser_role());

			if (getUserRole == null) {

				String SQL = "INSERT INTO ers.ers_user_roles\r\n"
						+ "(user_role)\r\n"
						+ "VALUES(?)\r\n"
						+ "RETURNING *";

				PreparedStatement ps = conn.prepareStatement(SQL);

				// Fill in values using PreparedStatement
				ps.setString(1, newRole.getUser_role().toLowerCase());

				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					ERSUserRoles createdRole = new ERSUserRoles(
							rs.getInt(1),
							rs.getString(2));
					log.info("Created new role - " + createdRole.getUser_role());
					return createdRole;
				}
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());

		} catch (Exception ex) {
			log.error(ex.getMessage());

		}
		return null;
	}

}
