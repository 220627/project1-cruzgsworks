package com.revature.ers.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.revature.ers.models.ERSReimbursement;
import com.revature.ers.models.ERSReimbursementStatus;
import com.revature.ers.models.ERSReimbursementType;
import com.revature.ers.models.ERSUsers;
import com.revature.ers.utils.ConnectionUtil;

public class ERSReimbursementDAO implements ERSReimbursementDAOInteface {

	@Override
	public ERSReimbursement addNewRequest(ERSReimbursement myReimb) {

		try (Connection conn = ConnectionUtil.getConnection()) {

			String SQL = "INSERT INTO ers.ers_reimbursement\r\n"
					+ "(reimb_amount, reimb_submitted, reimb_description, reimb_receipt, reimb_author, reimb_status_id, reimb_type_id)\r\n"
					+ "VALUES(?, ?, ?, ?, ?, ?, ?)\r\n"
					+ "RETURNING reimb_id, reimb_submitted, reimb_description, reimb_author, reimb_status_id, reimb_type_id";

			PreparedStatement ps = conn.prepareStatement(SQL);

			// Fill in values using PreparedStatement
			ps.setDouble(1, myReimb.getReimb_amount());
			ps.setTimestamp(2, myReimb.getReimb_submitted());
			ps.setString(3, myReimb.getReimb_description());
			ps.setBytes(4, myReimb.getReimb_receipt().length > 0 ? myReimb.getReimb_receipt() : null);
			ps.setInt(5, myReimb.getReimb_author());
			ps.setInt(6, myReimb.getReimb_status_id());
			ps.setInt(7, myReimb.getReimb_type_id());

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ERSReimbursement newReimb = new ERSReimbursement();
				newReimb.setReimb_id(rs.getInt(1));
				newReimb.setReimb_submitted(rs.getTimestamp(2));
				newReimb.setReimb_description(rs.getString(3));
				newReimb.setReimb_author(rs.getInt(4));
				newReimb.setReimb_status_id(rs.getInt(5));
				newReimb.setReimb_type_id(rs.getInt(6));
				return newReimb;
			}

		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	@Override
	public ArrayList<ERSReimbursement> getReimbursementRequest(int reimb_status_id, int reimb_author) {

		String SQL = "SELECT reimb_id, reimb_amount, reimb_submitted, reimb_resolved, reimb_description, reimb_author, reimb_resolver, ers.reimb_status_id, "
				+ "ert.reimb_type_id, ers.reimb_status, ert.reimb_type, eu.user_first_name, eu.user_last_name, eu.user_role_id, "
				+ "eu2.user_first_name AS resolver_first_name, eu2.user_last_name AS resolver_last_name, eu2.user_role_id AS resolver_role_id, "
				+ "CASE WHEN reimb_receipt IS NOT NULL THEN true ELSE false END AS has_receipt\r\n"
				+ "FROM ers.ers_reimbursement er\r\n"
				+ "LEFT JOIN ers.ers_reimbursement_status ers ON\r\n"
				+ "er.reimb_status_id = ers.reimb_status_id\r\n"
				+ "LEFT JOIN ers.ers_reimbursement_type ert ON\r\n"
				+ "er.reimb_type_id = ert.reimb_type_id\r\n"
				+ "LEFT JOIN ers.ers_users eu ON\r\n"
				+ "er.reimb_author = eu.ers_users_id\r\n"
				+ "LEFT JOIN ers.ers_users eu2 ON\r\n"
				+ "er.reimb_resolver = eu2.ers_users_id\r\n"
				+ "WHERE er.reimb_status_id = ?\r\n"
				+ "AND er.reimb_author = ?\r\n"
				+ "ORDER BY er.reimb_id ASC";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);
			ps.setInt(1, reimb_status_id);
			ps.setInt(2, reimb_author);
			// System.out.println(ps.toString());
			ResultSet getReimb = ps.executeQuery();

			ArrayList<ERSReimbursement> reimbList = new ArrayList<ERSReimbursement>();
			ERSUsers ersAuthor = new ERSUsers();
			ERSUsers ersResolver = new ERSUsers();
			ERSReimbursement reimbObj;
			ERSReimbursementStatus ers;
			ERSReimbursementType ert;
			while (getReimb.next()) {
				reimbObj = new ERSReimbursement();
				reimbObj.setReimb_id(getReimb.getInt(1));
				reimbObj.setReimb_amount(getReimb.getDouble(2));
				reimbObj.setReimb_submitted(getReimb.getTimestamp(3));
				reimbObj.setReimb_resolved(getReimb.getTimestamp(4));
				reimbObj.setReimb_description(getReimb.getString(5));
				reimbObj.setReimb_author(getReimb.getInt(6));
				reimbObj.setReimb_resolver(getReimb.getInt(7));
				reimbObj.setReimb_status_id(getReimb.getInt(8));
				reimbObj.setReimb_type_id(getReimb.getInt(9));
				reimbObj.setHas_receipt(getReimb.getBoolean("has_receipt"));

				ers = new ERSReimbursementStatus(getReimb.getInt(8), getReimb.getString(10));
				ert = new ERSReimbursementType(getReimb.getInt(9), getReimb.getString(11));

				ersAuthor.setUser_first_name(
						StringUtils.isNoneEmpty(getReimb.getString(12)) ? getReimb.getString(12) : "");
				ersAuthor.setUser_last_name(
						StringUtils.isNoneEmpty(getReimb.getString(13)) ? getReimb.getString(13) : "");

				ersResolver.setUser_first_name(
						StringUtils.isNoneEmpty(getReimb.getString(14)) ? getReimb.getString(14) : "");
				ersResolver.setUser_last_name(
						StringUtils.isNoneEmpty(getReimb.getString(15)) ? getReimb.getString(15) : "");

				reimbObj.setErsReimbursementStatus(ers);
				reimbObj.setErsReimbursementType(ert);
				reimbObj.setErsAuthor(ersAuthor);
				reimbObj.setErsResolver(ersResolver);

				reimbList.add(reimbObj);
				// System.out.println(reimbList.toString());
			}
			return reimbList;
		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	@Override
	public ArrayList<ERSReimbursement> getReimbursementRequestPagination(int reimb_status_id, int reimb_author, boolean isManager,
			int limit, int page) {

		String SQL = null;
		PreparedStatement ps = null;
		try (Connection conn = ConnectionUtil.getConnection()) {
			
			int offset = (page - 1) * limit;

			if (reimb_status_id < 1) {
				if (isManager) {
					SQL = "SELECT reimb_id, reimb_amount, reimb_submitted, reimb_resolved, reimb_description, reimb_author, reimb_resolver, ers.reimb_status_id, "
							+ "ert.reimb_type_id, ers.reimb_status, ert.reimb_type, eu.user_first_name, eu.user_last_name, eu.user_role_id, "
							+ "eu2.user_first_name AS resolver_first_name, eu2.user_last_name AS resolver_last_name, eu2.user_role_id AS resolver_role_id, "
							+ "CASE WHEN reimb_receipt IS NOT NULL THEN true ELSE false END AS has_receipt\r\n"
							+ "FROM ers.ers_reimbursement er\r\n"
							+ "LEFT JOIN ers.ers_reimbursement_status ers ON\r\n"
							+ "er.reimb_status_id = ers.reimb_status_id\r\n"
							+ "LEFT JOIN ers.ers_reimbursement_type ert ON\r\n"
							+ "er.reimb_type_id = ert.reimb_type_id\r\n"
							+ "LEFT JOIN ers.ers_users eu ON\r\n"
							+ "er.reimb_author = eu.ers_users_id\r\n"
							+ "LEFT JOIN ers.ers_users eu2 ON\r\n"
							+ "er.reimb_resolver = eu2.ers_users_id\r\n"
							+ "ORDER BY er.reimb_id ASC\r\n"
							+ "LIMIT ? OFFSET ?";
					ps = conn.prepareStatement(SQL);
					ps.setInt(1, limit);
					ps.setInt(2, offset);
				} else {
					SQL = "SELECT reimb_id, reimb_amount, reimb_submitted, reimb_resolved, reimb_description, reimb_author, reimb_resolver, ers.reimb_status_id, "
							+ "ert.reimb_type_id, ers.reimb_status, ert.reimb_type, eu.user_first_name, eu.user_last_name, eu.user_role_id, "
							+ "eu2.user_first_name AS resolver_first_name, eu2.user_last_name AS resolver_last_name, eu2.user_role_id AS resolver_role_id, "
							+ "CASE WHEN reimb_receipt IS NOT NULL THEN true ELSE false END AS has_receipt\r\n"
							+ "FROM ers.ers_reimbursement er\r\n"
							+ "LEFT JOIN ers.ers_reimbursement_status ers ON\r\n"
							+ "er.reimb_status_id = ers.reimb_status_id\r\n"
							+ "LEFT JOIN ers.ers_reimbursement_type ert ON\r\n"
							+ "er.reimb_type_id = ert.reimb_type_id\r\n"
							+ "LEFT JOIN ers.ers_users eu ON\r\n"
							+ "er.reimb_author = eu.ers_users_id\r\n"
							+ "LEFT JOIN ers.ers_users eu2 ON\r\n"
							+ "er.reimb_resolver = eu2.ers_users_id\r\n"
							+ "WHERE er.reimb_author = ?\r\n"
							+ "ORDER BY er.reimb_id ASC\r\n"
							+ "LIMIT ? OFFSET ?";
					ps = conn.prepareStatement(SQL);
					ps.setInt(1, reimb_author);
					ps.setInt(2, limit);
					ps.setInt(3, offset);
				}

				
			} else {
				if(isManager) {
					SQL = "SELECT reimb_id, reimb_amount, reimb_submitted, reimb_resolved, reimb_description, reimb_author, reimb_resolver, ers.reimb_status_id, "
							+ "ert.reimb_type_id, ers.reimb_status, ert.reimb_type, eu.user_first_name, eu.user_last_name, eu.user_role_id, "
							+ "eu2.user_first_name AS resolver_first_name, eu2.user_last_name AS resolver_last_name, eu2.user_role_id AS resolver_role_id, "
							+ "CASE WHEN reimb_receipt IS NOT NULL THEN true ELSE false END AS has_receipt\r\n"
							+ "FROM ers.ers_reimbursement er\r\n"
							+ "LEFT JOIN ers.ers_reimbursement_status ers ON\r\n"
							+ "er.reimb_status_id = ers.reimb_status_id\r\n"
							+ "LEFT JOIN ers.ers_reimbursement_type ert ON\r\n"
							+ "er.reimb_type_id = ert.reimb_type_id\r\n"
							+ "LEFT JOIN ers.ers_users eu ON\r\n"
							+ "er.reimb_author = eu.ers_users_id\r\n"
							+ "LEFT JOIN ers.ers_users eu2 ON\r\n"
							+ "er.reimb_resolver = eu2.ers_users_id\r\n"
							+ "WHERE er.reimb_status_id = ?\r\n"
							+ "ORDER BY er.reimb_id ASC\r\n"
							+ "LIMIT ? OFFSET ?";

					ps = conn.prepareStatement(SQL);
					ps.setInt(1, reimb_status_id);
					ps.setInt(2, limit);
					ps.setInt(3, offset);
				} else {
					SQL = "SELECT reimb_id, reimb_amount, reimb_submitted, reimb_resolved, reimb_description, reimb_author, reimb_resolver, ers.reimb_status_id, "
							+ "ert.reimb_type_id, ers.reimb_status, ert.reimb_type, eu.user_first_name, eu.user_last_name, eu.user_role_id, "
							+ "eu2.user_first_name AS resolver_first_name, eu2.user_last_name AS resolver_last_name, eu2.user_role_id AS resolver_role_id, "
							+ "CASE WHEN reimb_receipt IS NOT NULL THEN true ELSE false END AS has_receipt\r\n"
							+ "FROM ers.ers_reimbursement er\r\n"
							+ "LEFT JOIN ers.ers_reimbursement_status ers ON\r\n"
							+ "er.reimb_status_id = ers.reimb_status_id\r\n"
							+ "LEFT JOIN ers.ers_reimbursement_type ert ON\r\n"
							+ "er.reimb_type_id = ert.reimb_type_id\r\n"
							+ "LEFT JOIN ers.ers_users eu ON\r\n"
							+ "er.reimb_author = eu.ers_users_id\r\n"
							+ "LEFT JOIN ers.ers_users eu2 ON\r\n"
							+ "er.reimb_resolver = eu2.ers_users_id\r\n"
							+ "WHERE er.reimb_status_id = ?\r\n"
							+ "AND er.reimb_author = ?\r\n"
							+ "ORDER BY er.reimb_id ASC\r\n"
							+ "LIMIT ? OFFSET ?";

					ps = conn.prepareStatement(SQL);
					ps.setInt(1, reimb_status_id);
					ps.setInt(2, reimb_author);
					ps.setInt(3, limit);
					ps.setInt(4, offset);
				}
			}
			ResultSet getReimb = ps.executeQuery();

			ArrayList<ERSReimbursement> reimbList = new ArrayList<ERSReimbursement>();
			ERSUsers ersAuthor = null;
			ERSUsers ersResolver = null;
			ERSReimbursement reimbObj;
			ERSReimbursementStatus ers;
			ERSReimbursementType ert;
			while (getReimb.next()) {
				reimbObj = new ERSReimbursement();
				reimbObj.setReimb_id(getReimb.getInt("reimb_id"));
				reimbObj.setReimb_amount(getReimb.getDouble("reimb_amount"));
				reimbObj.setReimb_submitted(getReimb.getTimestamp("reimb_submitted"));
				reimbObj.setReimb_resolved(getReimb.getTimestamp("reimb_resolved"));
				reimbObj.setReimb_description(getReimb.getString("reimb_description"));
				reimbObj.setReimb_author(getReimb.getInt("reimb_author"));
				reimbObj.setReimb_resolver(getReimb.getInt("reimb_resolver"));
				reimbObj.setReimb_status_id(getReimb.getInt("reimb_status_id"));
				reimbObj.setReimb_type_id(getReimb.getInt("reimb_type_id"));
				reimbObj.setHas_receipt(getReimb.getBoolean("has_receipt"));

				ers = new ERSReimbursementStatus(getReimb.getInt("reimb_status_id"),
						getReimb.getString("reimb_status"));
				ert = new ERSReimbursementType(getReimb.getInt("reimb_type_id"), getReimb.getString("reimb_type"));

				ersAuthor = new ERSUsers();
				ersResolver = new ERSUsers();

				ersAuthor.setErs_users_id(getReimb.getInt("reimb_author"));
				ersAuthor.setUser_first_name(
						StringUtils.isNoneEmpty(getReimb.getString("user_first_name"))
								? getReimb.getString("user_first_name")
								: "");
				ersAuthor.setUser_last_name(
						StringUtils.isNoneEmpty(getReimb.getString("user_last_name"))
								? getReimb.getString("user_last_name")
								: "");
				ersAuthor.setUser_role_id(getReimb.getInt("user_role_id"));

				ersResolver.setErs_users_id(getReimb.getInt("reimb_resolver"));
				ersResolver.setUser_first_name(
						StringUtils.isNoneEmpty(getReimb.getString("resolver_first_name"))
								? getReimb.getString("resolver_first_name")
								: "");
				ersResolver.setUser_last_name(
						StringUtils.isNoneEmpty(getReimb.getString("resolver_last_name"))
								? getReimb.getString("resolver_last_name")
								: "");
				ersResolver.setUser_role_id(getReimb.getInt("resolver_role_id"));

				reimbObj.setErsReimbursementStatus(ers);
				reimbObj.setErsReimbursementType(ert);
				reimbObj.setErsAuthor(ersAuthor);
				reimbObj.setErsResolver(ersResolver);

				reimbList.add(reimbObj);
				// System.out.println(reimbList.toString());
			}
			return reimbList;
		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	@Override
	public ERSReimbursement getReceipt(int reimb_id, int ers_users_id) {

		String SQL = "SELECT reimb_receipt FROM ers.ers_reimbursement WHERE reimb_id = ?";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);
			ps.setInt(1, reimb_id);
			// System.out.println(ps.toString());
			ResultSet getReimb = ps.executeQuery();

			if (getReimb.next()) {
				ERSReimbursement reimbObj = new ERSReimbursement();
				reimbObj.setReimb_receipt(getReimb.getBytes(1));
				return reimbObj;
			}

		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	@Override
	public ArrayList<ERSReimbursement> getAllReimbursementRequests(int reimb_status_id) {

		String SQL = "SELECT reimb_id, reimb_amount, reimb_submitted, reimb_resolved, reimb_description, reimb_author, reimb_resolver, "
				+ "ers.reimb_status_id, ert.reimb_type_id, ers.reimb_status, ert.reimb_type, eu.user_first_name, eu.user_last_name, "
				+ "eu.user_role_id, eu2.user_first_name AS resolver_first_name, eu2.user_last_name AS resolver_last_name, eu2.user_role_id AS resolver_role_id, "
				+ "CASE WHEN reimb_receipt IS NOT NULL THEN true ELSE false END AS has_receipt\r\n"
				+ "FROM ers.ers_reimbursement er\r\n"
				+ "LEFT JOIN ers.ers_reimbursement_status ers ON\r\n"
				+ "er.reimb_status_id = ers.reimb_status_id\r\n"
				+ "LEFT JOIN ers.ers_reimbursement_type ert ON\r\n"
				+ "er.reimb_type_id = ert.reimb_type_id\r\n"
				+ "LEFT JOIN ers.ers_users eu ON\r\n"
				+ "er.reimb_author = eu.ers_users_id\r\n"
				+ "LEFT JOIN ers.ers_users eu2 ON\r\n"
				+ "er.reimb_resolver = eu2.ers_users_id\r\n"
				+ "WHERE er.reimb_status_id = ?\r\n"
				+ "ORDER BY er.reimb_id ASC;\r\n";

		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);
			ps.setInt(1, reimb_status_id);
			// System.out.println(ps.toString());
			ResultSet getReimb = ps.executeQuery();

			ArrayList<ERSReimbursement> reimbList = new ArrayList<ERSReimbursement>();
			ERSUsers ersAuthor = new ERSUsers();
			ERSUsers ersResolver = new ERSUsers();
			ERSReimbursement reimbObj;
			ERSReimbursementStatus ers;
			ERSReimbursementType ert;
			while (getReimb.next()) {
				reimbObj = new ERSReimbursement();
				reimbObj.setReimb_id(getReimb.getInt("reimb_id"));
				reimbObj.setReimb_amount(getReimb.getDouble("reimb_amount"));
				reimbObj.setReimb_submitted(getReimb.getTimestamp("reimb_submitted"));
				reimbObj.setReimb_resolved(getReimb.getTimestamp("reimb_resolved"));
				reimbObj.setReimb_description(getReimb.getString("reimb_description"));
				reimbObj.setReimb_author(getReimb.getInt("reimb_author"));
				reimbObj.setReimb_resolver(getReimb.getInt("reimb_resolver"));
				reimbObj.setReimb_status_id(getReimb.getInt("reimb_status_id"));
				reimbObj.setReimb_type_id(getReimb.getInt("reimb_type_id"));
				reimbObj.setHas_receipt(getReimb.getBoolean("has_receipt"));

				ers = new ERSReimbursementStatus(getReimb.getInt("reimb_status_id"),
						getReimb.getString("reimb_status"));
				ert = new ERSReimbursementType(getReimb.getInt("reimb_type_id"), getReimb.getString("reimb_type"));

				ersAuthor.setErs_users_id(getReimb.getInt("reimb_author"));
				ersAuthor.setUser_first_name(
						StringUtils.isNoneEmpty(getReimb.getString("user_first_name"))
								? getReimb.getString("user_first_name")
								: "");
				ersAuthor.setUser_last_name(
						StringUtils.isNoneEmpty(getReimb.getString("user_last_name"))
								? getReimb.getString("user_last_name")
								: "");
				ersAuthor.setUser_role_id(getReimb.getInt("user_role_id"));

				ersResolver.setErs_users_id(getReimb.getInt("reimb_resolver"));
				ersResolver.setUser_first_name(
						StringUtils.isNoneEmpty(getReimb.getString("resolver_first_name"))
								? getReimb.getString("resolver_first_name")
								: "");
				ersResolver.setUser_last_name(
						StringUtils.isNoneEmpty(getReimb.getString("resolver_last_name"))
								? getReimb.getString("resolver_last_name")
								: "");
				ersResolver.setUser_role_id(getReimb.getInt("resolver_role_id"));

				reimbObj.setErsReimbursementStatus(ers);
				reimbObj.setErsReimbursementType(ert);
				reimbObj.setErsAuthor(ersAuthor);
				reimbObj.setErsResolver(ersResolver);

				reimbList.add(reimbObj);
				// System.out.println(reimbList.toString());
			}
			return reimbList;
		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	@Override
	public ArrayList<ERSReimbursement> getAllReimbursementRequestsPagination(int reimb_status_id, int limit, int offset) {

		String SQL = null;
		PreparedStatement ps = null;
		try (Connection conn = ConnectionUtil.getConnection()) {
			if (reimb_status_id < 1) {
				SQL = "SELECT reimb_id, reimb_amount, reimb_submitted, reimb_resolved, reimb_description, reimb_author, reimb_resolver, "
						+ "ers.reimb_status_id, ert.reimb_type_id, ers.reimb_status, ert.reimb_type, eu.user_first_name, eu.user_last_name, "
						+ "eu.user_role_id, eu2.user_first_name AS resolver_first_name, eu2.user_last_name AS resolver_last_name, eu2.user_role_id AS resolver_role_id, "
						+ "CASE WHEN reimb_receipt IS NOT NULL THEN true ELSE false END AS has_receipt\r\n"
						+ "FROM ers.ers_reimbursement er\r\n"
						+ "LEFT JOIN ers.ers_reimbursement_status ers ON\r\n"
						+ "er.reimb_status_id = ers.reimb_status_id\r\n"
						+ "LEFT JOIN ers.ers_reimbursement_type ert ON\r\n"
						+ "er.reimb_type_id = ert.reimb_type_id\r\n"
						+ "LEFT JOIN ers.ers_users eu ON\r\n"
						+ "er.reimb_author = eu.ers_users_id\r\n"
						+ "LEFT JOIN ers.ers_users eu2 ON\r\n"
						+ "er.reimb_resolver = eu2.ers_users_id\r\n"
						+ "ORDER BY er.reimb_id ASC\r\n"
						+ "LIMIT ? OFFSET ?";
				ps = conn.prepareStatement(SQL);
				ps.setInt(1, limit);
				ps.setInt(2, offset);
			} else {
				SQL = "SELECT reimb_id, reimb_amount, reimb_submitted, reimb_resolved, reimb_description, reimb_author, reimb_resolver, "
						+ "ers.reimb_status_id, ert.reimb_type_id, ers.reimb_status, ert.reimb_type, eu.user_first_name, eu.user_last_name, "
						+ "eu.user_role_id, eu2.user_first_name AS resolver_first_name, eu2.user_last_name AS resolver_last_name, eu2.user_role_id AS resolver_role_id, "
						+ "CASE WHEN reimb_receipt IS NOT NULL THEN true ELSE false END AS has_receipt\r\n"
						+ "FROM ers.ers_reimbursement er\r\n"
						+ "LEFT JOIN ers.ers_reimbursement_status ers ON\r\n"
						+ "er.reimb_status_id = ers.reimb_status_id\r\n"
						+ "LEFT JOIN ers.ers_reimbursement_type ert ON\r\n"
						+ "er.reimb_type_id = ert.reimb_type_id\r\n"
						+ "LEFT JOIN ers.ers_users eu ON\r\n"
						+ "er.reimb_author = eu.ers_users_id\r\n"
						+ "LEFT JOIN ers.ers_users eu2 ON\r\n"
						+ "er.reimb_resolver = eu2.ers_users_id\r\n"
						+ "WHERE er.reimb_status_id = ?\r\n"
						+ "ORDER BY er.reimb_id ASC\r\n"
						+ "LIMIT ? OFFSET ?";
				ps = conn.prepareStatement(SQL);
				ps.setInt(1, reimb_status_id);
				ps.setInt(2, limit);
				ps.setInt(3, offset);
			}
			ResultSet getReimb = ps.executeQuery();

			ArrayList<ERSReimbursement> reimbList = new ArrayList<ERSReimbursement>();
			ERSUsers ersAuthor = new ERSUsers();
			ERSUsers ersResolver = new ERSUsers();
			ERSReimbursement reimbObj;
			ERSReimbursementStatus ers;
			ERSReimbursementType ert;
			while (getReimb.next()) {
				reimbObj = new ERSReimbursement();
				reimbObj.setReimb_id(getReimb.getInt("reimb_id"));
				reimbObj.setReimb_amount(getReimb.getDouble("reimb_amount"));
				reimbObj.setReimb_submitted(getReimb.getTimestamp("reimb_submitted"));
				reimbObj.setReimb_resolved(getReimb.getTimestamp("reimb_resolved"));
				reimbObj.setReimb_description(getReimb.getString("reimb_description"));
				reimbObj.setReimb_author(getReimb.getInt("reimb_author"));
				reimbObj.setReimb_resolver(getReimb.getInt("reimb_resolver"));
				reimbObj.setReimb_status_id(getReimb.getInt("reimb_status_id"));
				reimbObj.setReimb_type_id(getReimb.getInt("reimb_type_id"));
				reimbObj.setHas_receipt(getReimb.getBoolean("has_receipt"));

				ers = new ERSReimbursementStatus(getReimb.getInt("reimb_status_id"),
						getReimb.getString("reimb_status"));
				ert = new ERSReimbursementType(getReimb.getInt("reimb_type_id"), getReimb.getString("reimb_type"));

				ersAuthor.setErs_users_id(getReimb.getInt("reimb_author"));
				ersAuthor.setUser_first_name(
						StringUtils.isNoneEmpty(getReimb.getString("user_first_name"))
								? getReimb.getString("user_first_name")
								: "");
				ersAuthor.setUser_last_name(
						StringUtils.isNoneEmpty(getReimb.getString("user_last_name"))
								? getReimb.getString("user_last_name")
								: "");
				ersAuthor.setUser_role_id(getReimb.getInt("user_role_id"));

				ersResolver.setErs_users_id(getReimb.getInt("reimb_resolver"));
				ersResolver.setUser_first_name(
						StringUtils.isNoneEmpty(getReimb.getString("resolver_first_name"))
								? getReimb.getString("resolver_first_name")
								: "");
				ersResolver.setUser_last_name(
						StringUtils.isNoneEmpty(getReimb.getString("resolver_last_name"))
								? getReimb.getString("resolver_last_name")
								: "");
				ersResolver.setUser_role_id(getReimb.getInt("resolver_role_id"));

				reimbObj.setErsReimbursementStatus(ers);
				reimbObj.setErsReimbursementType(ert);
				reimbObj.setErsAuthor(ersAuthor);
				reimbObj.setErsResolver(ersResolver);

				reimbList.add(reimbObj);
				// System.out.println(reimbList.toString());
			}
			return reimbList;
		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	@Override
	public ERSReimbursement resolveReimbursements(int reimb_id, int ers_users_id, int reimb_status_id) {
		String SQL = "UPDATE ers_reimbursement SET reimb_status_id = ?, reimb_resolved = ?, reimb_resolver = ? WHERE reimb_id = ? RETURNING *";
		try (Connection conn = ConnectionUtil.getConnection()) {
			// Instantiate a PreparedStatement to fill in the variables (?)
			PreparedStatement ps = conn.prepareStatement(SQL);
			ps.setInt(1, reimb_status_id);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setInt(3, ers_users_id);
			ps.setInt(4, reimb_id);
			// System.out.println(ps.toString());
			ResultSet updateReimb = ps.executeQuery();

			if (updateReimb.next()) {
				ERSReimbursement reimbObj = new ERSReimbursement(
						updateReimb.getInt("reimb_id"),
						updateReimb.getDouble("reimb_amount"),
						updateReimb.getTimestamp("reimb_submitted"),
						updateReimb.getTimestamp("reimb_resolved"),
						updateReimb.getString("reimb_description"),
						updateReimb.getInt("reimb_author"),
						updateReimb.getInt("reimb_resolver"),
						updateReimb.getInt("reimb_status_id"),
						updateReimb.getInt("reimb_type_id"));
				return reimbObj;
			}

		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	@Override
	public int countReimbursements(boolean isManager, int author_id, int reimb_status_id) {
		String SQL = null;
		PreparedStatement ps = null;
		try (Connection conn = ConnectionUtil.getConnection()) {
			if (isManager) {
				if(reimb_status_id > 0) {
					SQL = "SELECT COUNT(*) FROM ers.ers_reimbursement WHERE reimb_status_id = ?";
					ps = conn.prepareStatement(SQL);
					ps.setInt(1, reimb_status_id);
				} else {
					SQL = "SELECT COUNT(*) FROM ers.ers_reimbursement";
					ps = conn.prepareStatement(SQL);
				}
				
			} else {
				if(reimb_status_id > 0) {
					SQL = "SELECT COUNT(*) FROM ers.ers_reimbursement WHERE reimb_author = ? AND reimb_status_id = ?";
					ps = conn.prepareStatement(SQL);
					ps.setInt(1, author_id);
					ps.setInt(2, reimb_status_id);
				} else {
					SQL = "SELECT COUNT(*) FROM ers.ers_reimbursement WHERE reimb_author = ?";
					ps = conn.prepareStatement(SQL);
					ps.setInt(1, author_id);
				}
				
			}

			ResultSet countReimb = ps.executeQuery();

			if (countReimb.next()) {
				return countReimb.getInt(1);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return 0;
	}

}
