package com.revature.ers.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.revature.ers.models.ERSReimbursementType;
import com.revature.ers.utils.ConnectionUtil;

public class ERSReimbursementTypeDAO implements ERSReimbursementTypeDAOInterface {

	@Override
	public ERSReimbursementType getReimbursementTypeById(int reimb_type_id) {
		String SQL = "SELECT * FROM ers.ers_reimbursement_type WHERE reimb_type_id = ?";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);

			ps.setInt(1, reimb_type_id);

			ResultSet getReimType = ps.executeQuery();

			if (getReimType.next()) {
				ERSReimbursementType getReimTypeObj = new ERSReimbursementType(
						getReimType.getInt(1),
						getReimType.getString(2));
				return getReimTypeObj;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	@Override
	public ERSReimbursementType getReimbursementTypeByReimbType(String reimb_type) {
		String SQL = "SELECT * FROM ers.ers_reimbursement_type WHERE reimb_type = ?";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);

			ps.setString(1, reimb_type);

			ResultSet getReimbType = ps.executeQuery();

			if (getReimbType.next()) {
				ERSReimbursementType getReimbTypeObj = new ERSReimbursementType(
						getReimbType.getInt(1),
						getReimbType.getString(2));
				return getReimbTypeObj;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
		return null;
	}

	@Override
	public ERSReimbursementType createReimbursementType(ERSReimbursementType newReimbursementType) {

		try (Connection conn = ConnectionUtil.getConnection()) {

			ERSReimbursementType reimbType = this.getReimbursementTypeByReimbType(newReimbursementType.getReimb_type());

			if (reimbType == null) {

				// TODO create roles table and return to this
				String SQL = "INSERT INTO ers.ers_reimbursement_type\r\n"
						+ "(reimb_type)\r\n"
						+ "VALUES(?)\r\n"
						+ "RETURNING *";

				PreparedStatement ps = conn.prepareStatement(SQL);

				// Fill in values using PreparedStatement
				ps.setString(1, newReimbursementType.getReimb_type().toLowerCase());

				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					ERSReimbursementType createdReimbType = new ERSReimbursementType(
							rs.getInt(1),
							rs.getString(2));

					return createdReimbType;
				}
			}

		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	public ArrayList<ERSReimbursementType> getAllReimbursementTypeByReimbTypes() {
		String SQL = "SELECT * FROM ers.ers_reimbursement_type ert ORDER BY ert.reimb_type_id ASC";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);

			ResultSet getReimType = ps.executeQuery();

			ArrayList<ERSReimbursementType> typesList = new ArrayList<ERSReimbursementType>();
			while (getReimType.next()) {
				typesList.add(new ERSReimbursementType(
						getReimType.getInt(1),
						getReimType.getString(2)));
			}
			return typesList;
		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

}
