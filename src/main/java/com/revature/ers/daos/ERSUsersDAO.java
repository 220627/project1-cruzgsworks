package com.revature.ers.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.revature.ers.models.ERSUsers;
import com.revature.ers.utils.AuthUtil;
import com.revature.ers.utils.ConnectionUtil;

public class ERSUsersDAO implements ERSUsersDAOInterface {

	@Override
	public ERSUsers getUserByUserName(String ers_username) {
		String SQL = "SELECT * FROM ers.ers_users WHERE ers_username = ?";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);

			ps.setString(1, ers_username);

			ResultSet getUser = ps.executeQuery();

			if (getUser.next()) {
				ERSUsers getUserObj = new ERSUsers(
						getUser.getInt(1),
						getUser.getString(2),
						getUser.getString(3),
						getUser.getString(4),
						getUser.getString(5),
						getUser.getString(6),
						getUser.getInt(7));

				return getUserObj;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	@Override
	public ERSUsers createUser(ERSUsers newUser) {

		try (Connection conn = ConnectionUtil.getConnection()) {

			ERSUsers getUser = this.getUserByUserName(newUser.getErs_username());

			if (getUser == null) {

				// TODO create roles table and return to this
				String SQL = "INSERT INTO ers.ers_users\r\n"
						+ "(ers_username, ers_password, user_first_name, user_last_name, user_email, user_role_id)\r\n"
						+ "VALUES(?, ?, ?, ?, ?, ?)\r\n"
						+ "RETURNING *";

				PreparedStatement ps = conn.prepareStatement(SQL);

				// Fill in values using PreparedStatement
				ps.setString(1, newUser.getErs_username().toLowerCase());
				ps.setString(2, AuthUtil.doEncrypt(newUser.getErs_password()));
				ps.setString(3, newUser.getUser_first_name());
				ps.setString(4, newUser.getUser_last_name());
				ps.setString(5, newUser.getUser_email().toLowerCase());
				ps.setInt(6, newUser.getUser_role_id());

				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					ERSUsers createdUser = new ERSUsers(
							rs.getInt(1),
							rs.getString(2),
							rs.getString(3),
							rs.getString(4),
							rs.getString(5),
							rs.getString(6),
							rs.getInt(7));

					return createdUser;
				}
			}

		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

}
