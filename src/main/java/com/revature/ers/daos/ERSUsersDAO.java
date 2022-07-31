package com.revature.ers.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.ers.models.ERSUsers;
import com.revature.ers.utils.AuthUtil;
import com.revature.ers.utils.ConnectionUtil;

public class ERSUsersDAO implements ERSUsersDAOInterface {

	public static Logger log = LogManager.getLogger();

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
						getUser.getInt("ers_users_id"),
						getUser.getString("ers_username"),
						getUser.getString("ers_password"),
						getUser.getString("user_first_name"),
						getUser.getString("user_last_name"),
						getUser.getString("user_email"),
						getUser.getInt("user_role_id"));

				log.info("Retrieved user - " + getUser.getString("user_first_name") + " "
						+ getUser.getString("user_last_name"));

				return getUserObj;
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return null;
	}

	@Override
	public ERSUsers getUserByUserId(int ers_users_id) {
		String SQL = "SELECT * FROM ers.ers_users WHERE ers_users_id = ?";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);

			ps.setInt(1, ers_users_id);

			ResultSet getUser = ps.executeQuery();

			if (getUser.next()) {
				ERSUsers getUserObj = new ERSUsers(
						getUser.getInt("ers_users_id"),
						getUser.getString("ers_username"),
						getUser.getString("user_first_name"),
						getUser.getString("user_last_name"),
						getUser.getString("user_email"),
						getUser.getInt("user_role_id"));

				log.info("Retrieved user - " + getUser.getString("user_first_name") + " "
						+ getUser.getString("user_last_name"));

				return getUserObj;
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return null;
	}

	@Override
	public ERSUsers createUser(ERSUsers newUser) {

		try (Connection conn = ConnectionUtil.getConnection()) {

			// ERSUsers getUser = this.getUserByUserName(newUser.getErs_username());

			// if (getUser == null) {

			String SQL = "INSERT INTO ers.ers_users\r\n"
					+ "(ers_username, ers_password, user_first_name, user_last_name, user_email, user_role_id)\r\n"
					+ "VALUES(?, ?, ?, ?, ?, ?)\r\n" + "RETURNING *";

			PreparedStatement ps = conn.prepareStatement(SQL);

			// System.out.println(newUser.getUser_role_id());

			// Fill in values using PreparedStatement
			ps.setString(1, newUser.getErs_username().toLowerCase());
			ps.setString(2, AuthUtil.doEncrypt(newUser.getErs_password()));
			ps.setString(3, newUser.getUser_first_name());
			ps.setString(4, newUser.getUser_last_name());
			ps.setString(5, newUser.getUser_email().toLowerCase());
			ps.setInt(6, newUser.getUser_role_id());

			// System.out.println(ps.toString());

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ERSUsers createdUser = new ERSUsers(
						rs.getInt("ers_users_id"),
						rs.getString("ers_username"),
						rs.getString("ers_password"),
						rs.getString("user_first_name"),
						rs.getString("user_last_name"),
						rs.getString("user_email"),
						rs.getInt("user_role_id"));

				log.info("Created new user - " + createdUser.getUser_first_name() + " "
						+ createdUser.getUser_last_name());

				return createdUser;
			}
			// }

		} catch (SQLException ex) {
			log.error(ex.getMessage());

		} catch (Exception ex) {
			log.error(ex.getMessage());

		}
		return null;
	}

	@Override
	public ERSUsers updateUser(ERSUsers updatedInfo) {

		try (Connection conn = ConnectionUtil.getConnection()) {

			String SQL = "UPDATE ers.ers_users\r\n"
					+ "SET ers_username = ?, user_first_name = ?, user_last_name = ?, user_email = ?\r\n"
					+ "WHERE ers_users_id = ?"
					+ "RETURNING *";

			PreparedStatement ps = conn.prepareStatement(SQL);

			// Fill in values using PreparedStatement
			ps.setString(1, updatedInfo.getErs_username().toLowerCase());
			ps.setString(2, updatedInfo.getUser_first_name());
			ps.setString(3, updatedInfo.getUser_last_name());
			ps.setString(4, updatedInfo.getUser_email().toLowerCase());
			ps.setInt(5, updatedInfo.getErs_users_id());

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ERSUsers updatedUser = new ERSUsers(
						rs.getInt("ers_users_id"),
						rs.getString("ers_username"),
						rs.getString("ers_password"),
						rs.getString("user_first_name"),
						rs.getString("user_last_name"),
						rs.getString("user_email"),
						rs.getInt("user_role_id"));

				log.info("Updated user - " + updatedUser.getUser_first_name() + " "
						+ updatedUser.getUser_last_name());

				return updatedUser;
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());

		} catch (Exception ex) {
			log.error(ex.getMessage());

		}
		return null;
	}

	@Override
	public ERSUsers updatePassword(ERSUsers updatedPassword) {

		try (Connection conn = ConnectionUtil.getConnection()) {

			String SQL = "UPDATE ers.ers_users\r\n"
					+ "SET ers_password = ?\r\n"
					+ "WHERE ers_users_id = ?"
					+ "RETURNING *";

			PreparedStatement ps = conn.prepareStatement(SQL);

			// Fill in values using PreparedStatement
			ps.setString(1, AuthUtil.doEncrypt(updatedPassword.getErs_password()));
			ps.setInt(2, updatedPassword.getErs_users_id());

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ERSUsers updatedUser = new ERSUsers(
						rs.getInt("ers_users_id"),
						rs.getString("ers_username"),
						rs.getString("ers_password"),
						rs.getString("user_first_name"),
						rs.getString("user_last_name"),
						rs.getString("user_email"),
						rs.getInt("user_role_id"));

				log.info("Updated password for - " + updatedUser.getUser_first_name() + " "
						+ updatedUser.getUser_last_name());

				return updatedUser;
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());

		} catch (Exception ex) {
			log.error(ex.getMessage());

		}
		return null;
	}

	public ERSUsers createManager(ERSUsers newManager) {

		try (Connection conn = ConnectionUtil.getConnection()) {

			String SQL = "INSERT INTO ers.ers_users\r\n"
					+ "(ers_username, ers_password, user_first_name, user_last_name, user_email, user_role_id)\r\n"
					+ "VALUES(?, ?, ?, ?, ?, ?)\r\n" + "RETURNING *";

			PreparedStatement ps = conn.prepareStatement(SQL);

			// Fill in values using PreparedStatement
			ps.setString(1, newManager.getErs_username().toLowerCase());
			ps.setString(2, AuthUtil.doEncrypt(newManager.getErs_password()));
			ps.setString(3, newManager.getUser_first_name());
			ps.setString(4, newManager.getUser_last_name());
			ps.setString(5, newManager.getUser_email().toLowerCase());
			ps.setInt(6, newManager.getUser_role_id());

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ERSUsers createdUser = new ERSUsers(
						rs.getInt("ers_users_id"),
						rs.getString("ers_username"),
						rs.getString("ers_password"),
						rs.getString("user_first_name"),
						rs.getString("user_last_name"),
						rs.getString("user_email"),
						rs.getInt("user_role_id"));

				log.info("Created new user - " + createdUser.getUser_first_name() + " "
						+ createdUser.getUser_last_name());

				return createdUser;
			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());

		} catch (Exception ex) {
			log.error(ex.getMessage());

		}
		return null;
	}

}
