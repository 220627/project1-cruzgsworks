package com.revature.ers.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.ers.models.ERSReimbursementStatus;
import com.revature.ers.utils.ConnectionUtil;

public class ERSReimbursementStatusDAO implements ERSReimbursementStatusDAOInterface {
	
	public static Logger log = LogManager.getLogger();

	@Override
	public ERSReimbursementStatus getReimbursementStatusById(int reimb_status_id) {
		String SQL = "SELECT * FROM ers.ers_reimbursement_status WHERE reimb_status_id = ?";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);

			ps.setInt(1, reimb_status_id);

			ResultSet getReimbStatus = ps.executeQuery();

			if (getReimbStatus.next()) {
				ERSReimbursementStatus getReimbStatusObj = new ERSReimbursementStatus(
						getReimbStatus.getInt(1),
						getReimbStatus.getString(2));
				log.info("Retrieved status by ID - " + getReimbStatusObj.getReimb_status());
				return getReimbStatusObj;
			}
		} catch (SQLException ex) {
			log.error(ex.getMessage());

		} catch (Exception ex) {
			log.error(ex.getMessage());

		}
		return null;
	}

	@Override
	public ERSReimbursementStatus getReimbursementStatusByStatus(String reimb_status) {
		String SQL = "SELECT * FROM ers.ers_reimbursement_status WHERE reimb_status = ?";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);

			ps.setString(1, reimb_status);

			ResultSet getReimbStatus = ps.executeQuery();

			if (getReimbStatus.next()) {
				ERSReimbursementStatus getReimbStatusObj = new ERSReimbursementStatus(
						getReimbStatus.getInt(1),
						getReimbStatus.getString(2));
				log.info("Retrieved status by name - " + getReimbStatusObj.getReimb_status());
				return getReimbStatusObj;
			}
		} catch (SQLException ex) {
			log.error(ex.getMessage());

		}
		return null;
	}

	@Override
	public ERSReimbursementStatus createReimbursementStatus(ERSReimbursementStatus newReimbursementStatus) {

		try (Connection conn = ConnectionUtil.getConnection()) {

			ERSReimbursementStatus reimbStatus = this
					.getReimbursementStatusByStatus(newReimbursementStatus.getReimb_status());

			if (reimbStatus == null) {

				String SQL = "INSERT INTO ers.ers_reimbursement_status\r\n"
						+ "(reimb_status)\r\n"
						+ "VALUES(?)\r\n"
						+ "RETURNING *";

				PreparedStatement ps = conn.prepareStatement(SQL);

				// Fill in values using PreparedStatement
				ps.setString(1, newReimbursementStatus.getReimb_status().toLowerCase());

				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					ERSReimbursementStatus createdReimbStatus = new ERSReimbursementStatus(
							rs.getInt(1),
							rs.getString(2));
					log.info("Created - " + createdReimbStatus.getReimb_status());
					return createdReimbStatus;
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
