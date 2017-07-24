package edu.ncrn.cornell.ced2ar.ced2ardata2ddi.util;

import org.springframework.web.multipart.MultipartFile;
import org.swordapp.client.AuthCredentials;
import org.swordapp.client.Deposit;
import org.swordapp.client.DepositReceipt;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDCollection;
import org.swordapp.client.ServiceDocument;

/**
 * This class is makes a deposit using simple sword client. Returns the deposit
 * receipt that contains uri of the deposit just made
 * 
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team
 */

public class SwordDeposit {

	public DepositReceipt DepositDataFile(MultipartFile file) throws Exception {
		SWORDClient client = new SWORDClient();
		ApplicationConfig applicationConfig = new ApplicationConfig();
		ServiceDocument sd = client.getServiceDocument(
				applicationConfig.getSwordServiceDocumentURL(),
				new AuthCredentials("dummy", "dummy"));
		Deposit deposit = new Deposit();
		deposit.setFile(file.getInputStream());
		deposit.setFilename(file.getOriginalFilename());
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);
		DepositReceipt receipt = client.deposit(
				col,
				deposit,
				new AuthCredentials(applicationConfig
						.getSwordAuthorizationUserId(), applicationConfig
						.getSwordAuthorizationUserPwd()));
		return receipt;
	}

	public DepositReceipt DepositDataFileByStreaming(MultipartFile file)
			throws Exception {
		SWORDClient client = new SWORDClient();
		ApplicationConfig applicationConfig = new ApplicationConfig();
		ServiceDocument sd = client.getServiceDocument(
				applicationConfig.getSwordServiceDocumentURL(),
				new AuthCredentials("dummy", "dummy"));
		Deposit deposit = new Deposit();
		deposit.setFile(file.getInputStream());
		deposit.setFilename(file.getOriginalFilename());
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);
		DepositReceipt receipt = client.deposit(
				col,
				deposit,
				new AuthCredentials(applicationConfig
						.getSwordAuthorizationUserId(), applicationConfig
						.getSwordAuthorizationUserPwd()));
		return receipt;
	}
}