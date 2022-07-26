package com.revature.ers.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.revature.ers.daos.ERSReimbursementDAO;
import com.revature.ers.daos.ERSReimbursementStatusDAO;
import com.revature.ers.daos.ERSUserRolesDAO;
import com.revature.ers.models.ERSReimbursement;
import com.revature.ers.models.ERSReimbursementCount;
import com.revature.ers.models.ERSReimbursementStatus;
import com.revature.ers.models.ERSUserRoles;
import com.revature.ers.models.ERSUsers;
import com.revature.ers.models.Responses;
import com.revature.ers.utils.AuthUtil;

import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;

public class ERSReimbursementController {

	public static Logger log = LogManager.getLogger();

	public static Handler getReimbursementRequestsPagination = (ctx) -> {

		// object of gson class
		Gson gson = new Gson();

		// retrieve user records
		ArrayList<ERSReimbursement> reimbRequest = null;
		ERSUsers eu = null;
		try {

			// Number of results per page
			int limit = StringUtils.isNoneEmpty(ctx.queryParam("limit")) ? Integer.parseInt(ctx.queryParam("limit"))
					: 5;

			// Page number
			int page = StringUtils.isNoneEmpty(ctx.queryParam("page")) ? Integer.parseInt(ctx.queryParam("page")) : 1;
			
			// Result order
			String orderBy = StringUtils.isNoneEmpty(ctx.queryParam("order")) && ctx.queryParam("order").equals("desc") ? "desc" : "asc";
			System.out.println(orderBy);
			
			// Order by column
			String column = StringUtils.isNoneEmpty(ctx.queryParam("column")) && !ctx.queryParam("column").equals("reimb_id") ? ctx.queryParam("column") : "reimb_id";
			System.out.println(column);

			// Filter by status
			String reimb_status = ctx.queryParam("reimb_status");
			int reimb_status_id = 0;
			if (StringUtils.isNotEmpty(reimb_status)) {
				ERSReimbursementStatus ers = new ERSReimbursementStatusDAO()
						.getReimbursementStatusByStatus(reimb_status);
				reimb_status_id = ers != null ? ers.getReimb_status_id() : 0;
			}

			// Determine which user is using this endpoint
			eu = AuthUtil.verifyCookie(ctx.cookie("Authentication"));
			ERSUserRoles curRole = new ERSUserRolesDAO().getRoleById(eu.getUser_role_id());

			// Check if user is Finance Manager
			boolean isManager = curRole.getUser_role().toLowerCase().equals("manager");

			reimbRequest = new ERSReimbursementDAO()
					.getReimbursementRequestPagination(reimb_status_id, eu.getErs_users_id(), isManager, limit, page, orderBy, column);

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		// object of responses class
		Responses response;

		// Response
		if (reimbRequest != null) {
			log.info(eu.getUser_first_name() + " " + eu.getUser_last_name() + " retrieved " + reimbRequest.size()
					+ " reimbursement requests");
			response = new Responses(200, "Retrieved list of reimbursements", true, reimbRequest);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not retrieve reimbursements", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}

	};

	public static Handler resolveReimbursements = (ctx) -> {
		Gson gson = new Gson();

		// retrieve user records
		ERSReimbursement updateReimb = null;
		ERSUsers curUser = null;
		ERSReimbursementStatus requestData = null;
		try {

			requestData = new ERSReimbursementStatusDAO().getReimbursementStatusByStatus(
					gson.fromJson(ctx.body(), ERSReimbursementStatus.class).getReimb_status());

			curUser = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			int reimb_id = Integer.parseInt(ctx.pathParam("reimb_id"));

			updateReimb = new ERSReimbursementDAO()
					.resolveReimbursements(reimb_id, curUser.getErs_users_id(), requestData.getReimb_status_id());

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		Responses response;

		// Response
		if (updateReimb != null) {
			log.info(curUser.getUser_first_name() + " " + curUser.getUser_last_name()
					+ " updated the status of a reimbursement request (ID # " + updateReimb.getReimb_id() + ") to "
					+ requestData.getReimb_status());
			response = new Responses(200, "Updated status of reimbursement", true, updateReimb);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not update status of reimbursement", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}
	};

	public static Handler getAllReimbursementRequests = (ctx) -> {

		// object of gson class
		Gson gson = new Gson();

		// retrieve user records
		ArrayList<ERSReimbursement> reimbRequest = null;
		ERSUsers eu = null;
		try {
			ERSReimbursementStatus ers = new ERSReimbursementStatusDAO()
					.getReimbursementStatusByStatus(ctx.queryParam("reimb_status"));

			eu = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			reimbRequest = new ERSReimbursementDAO()
					.getAllReimbursementRequests(ers.getReimb_status_id());
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		// object of responses class
		Responses response;

		// Response
		if (reimbRequest != null) {
			log.info(eu.getUser_first_name() + " " + eu.getUser_last_name() + " retrieved " + reimbRequest.size()
					+ " reimbursement requests");
			response = new Responses(200, "Retrieved list of reimbursements", true, reimbRequest);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not retrieve reimbursements", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}
	};

	public static Handler getReceipt = (ctx) -> {
		// retrieve user records
		ERSReimbursement reimbRequest = null;
		ERSUsers eu = null;
		try {

			int reimb_id = Integer.parseInt(ctx.pathParam("reimb_id"));

			eu = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			reimbRequest = new ERSReimbursementDAO()
					.getReceipt(reimb_id, eu.getErs_users_id());

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		// Response
		if (reimbRequest != null) {

			TikaConfig tika = new TikaConfig();
			Metadata metaData = new Metadata();

			InputStream is = new ByteArrayInputStream(reimbRequest.getReimb_receipt());

			MediaType mediaType = tika.getDetector().detect(TikaInputStream.get(is), metaData);
			MimeType mimeType = tika.getMimeRepository().forName(mediaType.toString());
			log.info(eu.getUser_first_name() + " " + eu.getUser_last_name()
					+ " is trying to download file from reimbursement ID " + reimbRequest.getReimb_id());
			ctx.status(200)
					.header("Content-Disposition",
							"inline; filename=\"" + System.currentTimeMillis() + mimeType.getExtension() + "\"")
					.contentType(mediaType.getType() + "/" + mediaType.getSubtype()).result(is);
		} else {
			ctx.status(404).result("Not Found");
		}
	};

	public static Handler getReimbursementRequests = (ctx) -> {
		// object of gson class
		Gson gson = new Gson();

		// retrieve user records
		ERSUsers eu = null;
		ArrayList<ERSReimbursement> reimbRequest = null;
		try {
			ERSReimbursementStatus ers = new ERSReimbursementStatusDAO()
					.getReimbursementStatusByStatus(ctx.queryParam("reimb_status"));

			eu = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			reimbRequest = new ERSReimbursementDAO()
					.getReimbursementRequest(ers.getReimb_status_id(), eu.getErs_users_id());
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		// object of responses class
		Responses response;

		// Response
		if (reimbRequest != null) {
			log.info(eu.getUser_first_name() + " " + eu.getUser_last_name() + " retrieved " + reimbRequest.size()
					+ " reimbursement requests");
			response = new Responses(200, "Retrieved list of reimbursements", true, reimbRequest);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not retrieve reimbursements", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}

	};
	public static Handler newReimbursementRequest = (ctx) -> {
		// object of gson class
		Gson gson = new Gson();

		Responses response;
		ERSReimbursement myReimb = null;
		ERSUsers curUser = null;
		try {
			UploadedFile uploadedReceipt = ctx.uploadedFile("rReceipt");
			byte[] myReceipt = ByteStreams.toByteArray(uploadedReceipt.getContent());

			Double rAmount = StringUtils.isNotEmpty(ctx.formParam("rAmount"))
					? Double.parseDouble(ctx.formParam("rAmount"))
					: null;
			int rType = StringUtils.isNotEmpty(ctx.formParam("rType")) ? Integer.parseInt(ctx.formParam("rType"))
					: null;
			String rDescription = StringUtils.isNotEmpty(ctx.formParam("rDescription")) ? ctx.formParam("rDescription")
					: null;

			curUser = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			myReimb = new ERSReimbursement(rAmount, new Timestamp(System.currentTimeMillis()),
					rDescription, myReceipt, curUser.getErs_users_id(),
					new ERSReimbursementStatusDAO().getReimbursementStatusByStatus("pending").getReimb_status_id(),
					rType);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		ERSReimbursement createReimb = new ERSReimbursementDAO().addNewRequest(myReimb);

		if (createReimb != null) {
			log.info(curUser.getUser_first_name() + " " + curUser.getUser_last_name() + " created a new reimbursement");
			response = new Responses(200, "Successfully created a new reimbursement request", true, createReimb);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not create a reimbursement request", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}

	};

	public static Handler countReimbursements = (ctx) -> {
		Gson gson = new Gson();

		int pages = 0;

		ERSUsers curUser = AuthUtil.verifyCookie(ctx.cookie("Authentication"));
		ERSUserRoles curRole = new ERSUserRolesDAO().getRoleById(curUser.getUser_role_id());
		String reimb_status = ctx.queryParam("reimb_status");
		int reimb_status_id = 0;
		if (StringUtils.isNotEmpty(reimb_status)) {
			ERSReimbursementStatus ers = new ERSReimbursementStatusDAO().getReimbursementStatusByStatus(reimb_status);
			reimb_status_id = ers != null ? ers.getReimb_status_id() : 0;
		}

		boolean isManager = curRole.getUser_role().toLowerCase().equals("manager");

		pages = new ERSReimbursementDAO().countReimbursements(isManager, curUser.getErs_users_id(), reimb_status_id);

		ERSReimbursementCount erc = new ERSReimbursementCount();
		erc.setCount_rows(pages);

		Responses response = null;

		response = new Responses(200, "Retrieved number of reimbursements", true, erc);
		ctx.status(response.getStatusCode()).json(gson.toJson(response));

	};

}
