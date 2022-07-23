package com.revature.ers.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.revature.ers.daos.ERSReimbursementDAO;
import com.revature.ers.daos.ERSReimbursementStatusDAO;
import com.revature.ers.models.ERSReimbursement;
import com.revature.ers.models.ERSReimbursementStatus;
import com.revature.ers.models.ERSUsers;
import com.revature.ers.models.Responses;
import com.revature.ers.utils.AuthUtil;

import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;

public class ERSReimbursementController {

	public static Handler resolveReimbursements = (ctx) -> {
		Gson gson = new Gson();

		// retrieve user records
		ERSReimbursement updateReimb = null;
		try {

			ERSReimbursementStatus requestData = new ERSReimbursementStatusDAO().getReimbursementStatusByStatus(
					gson.fromJson(ctx.body(), ERSReimbursementStatus.class).getReimb_status());
			
			ERSUsers curUser = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			int reimb_id = Integer.parseInt(ctx.pathParam("reimb_id"));

			updateReimb = new ERSReimbursementDAO()
					.resolveReimbursements(reimb_id, curUser.getErs_users_id(), requestData.getReimb_status_id());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Responses response;

		// Response
		if (updateReimb != null) {
			response = new Responses(200, "Retrieved list of reimbursements", true, updateReimb);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not retrieve reimbursements", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}
	};

	public static Handler getAllReimbursementRequests = (ctx) -> {

		// object of gson class
		Gson gson = new Gson();

		// retrieve user records
		ArrayList<ERSReimbursement> reimbRequest = null;
		try {
			ERSReimbursementStatus ers = new ERSReimbursementStatusDAO()
					.getReimbursementStatusByStatus(ctx.queryParam("reimb_status"));

			// ERSUsers eu = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			reimbRequest = new ERSReimbursementDAO()
					.getAllReimbursementRequests(ers.getReimb_status_id());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// object of responses class
		Responses response;

		// Response
		if (reimbRequest != null) {
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
		try {

			int reimb_id = Integer.parseInt(ctx.pathParam("reimb_id"));

			ERSUsers eu = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			reimbRequest = new ERSReimbursementDAO()
					.getReceipt(reimb_id, eu.getErs_users_id());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Response
		if (reimbRequest != null) {

			TikaConfig tika = new TikaConfig();
			Metadata metaData = new Metadata();

			InputStream is = new ByteArrayInputStream(reimbRequest.getReimb_receipt());

			MediaType mediaType = tika.getDetector().detect(TikaInputStream.get(is), metaData);
			MimeType mimeType = tika.getMimeRepository().forName(mediaType.toString());

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
		ArrayList<ERSReimbursement> reimbRequest = null;
		try {
			ERSReimbursementStatus ers = new ERSReimbursementStatusDAO()
					.getReimbursementStatusByStatus(ctx.queryParam("reimb_status"));

			ERSUsers eu = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			reimbRequest = new ERSReimbursementDAO()
					.getReimbursementRequest(ers.getReimb_status_id(), eu.getErs_users_id());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// object of responses class
		Responses response;

		// Response
		if (reimbRequest != null) {
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

			ERSUsers curUser = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			myReimb = new ERSReimbursement(rAmount, new Timestamp(System.currentTimeMillis()),
					rDescription, myReceipt, curUser.getErs_users_id(),
					new ERSReimbursementStatusDAO().getReimbursementStatusByStatus("pending").getReimb_status_id(),
					rType);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		ERSReimbursement createReimb = new ERSReimbursementDAO().addNewRequest(myReimb);

		if (createReimb != null) {
			response = new Responses(200, "Successfully created a new reimbursement request", true, createReimb);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not create a reimbursement request", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}

	};
}
