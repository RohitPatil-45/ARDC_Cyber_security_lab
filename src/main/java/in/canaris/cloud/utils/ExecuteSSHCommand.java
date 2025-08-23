package in.canaris.cloud.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import in.canaris.cloud.repository.KVMDriveDetailsRepository;

public class ExecuteSSHCommand {
//	@Autowired
//	private KVMDriveDetailsRepository kvmDriveDetailsRepository;

	public CommandResult executeCommand(String command, String USER, String PASSWORD, String HOST) {

		Session session = null;
		ChannelExec channel = null;
		boolean status = true;
		StringBuilder message = new StringBuilder();

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(USER, HOST, 22);
			session.setPassword(PASSWORD);

			// Configuring SSH to ignore key checking
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);

			InputStream errStream = channel.getErrStream();
			InputStream in = channel.getInputStream();
			channel.connect();

			byte[] buffer = new byte[1024];
			int readCount;
			while ((readCount = in.read(buffer)) != -1) {
				System.out.print("CMD op:" + new String(buffer, 0, readCount));
			}
			byte[] errorBuffer = new byte[1024];
			while ((readCount = errStream.read(errorBuffer)) != -1) {
				String errorOutput = new String(errorBuffer, 0, readCount);
				System.out.println("errorOutput:" + errorOutput + ":@");
				if (errorOutput.toLowerCase().contains("error")) {
					System.out.println("$$$$$$$$$$$$$$$: CMD err: " + errorOutput + ":#");
					status = false;
					message.append("Error: " + errorOutput);
				}
			}
			if (status != false) {
				status = true;
				message.append("Command executed successfully.");
				message.append(System.lineSeparator());
			}

		} catch (Exception e) {
			System.out.print("Exception command:" + e.getMessage());
			status = false;
			message = message.append("Error: " + e.getMessage());
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}

		return new CommandResult(status, message);
	}
	
	

}
