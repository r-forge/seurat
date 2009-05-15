package RConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;


public class RConnectionManager {
	
	public void connectToR() throws InterruptedException, RserveException{
	/*
		try {
		this.startRServeUnterWindows();
		}
		catch (Exception e) {}
		//this.checkLocalRserve();// JOptionPane.showMessageDialog(this,"Connection
		// to R failed...");
		 * 
		 */
		
		//System.out.println("result="+Srs.checkLocalRserve());
		try {
		    RConnection c=new RConnection();
		    c.shutdown();
		} catch (Exception x) {};
		this.startRServeUnterWindows();
		Srs.checkLocalRserve();
	
	
		
		//RConnection c = new RConnection();
		//c.close();
		Thread.sleep(1000);
		
		
	}
	
	
	
	
	class StreamHog extends Thread {
		InputStream is;

		StreamHog(InputStream is) {
			this.is = is;
			start();
		}

		@Override
		public void run() {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println("Rserve>" + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean checkRServe() {
		try {
			RConnection r = new RConnection();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean launchRserve(String cmd) {
		System.out.println();
		System.out.println(cmd);
		System.out.println();
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			System.out.println("waiting for Rserve to start ... (" + p + ")");
			// we need to fetch the output - some platforms will die if you
			// don't ...
			StreamHog errorHog = new StreamHog(p.getErrorStream());
			StreamHog outputHog = new StreamHog(p.getInputStream());
			p.waitFor();
			System.out.println("call terminated, let us try to connect ...");
		} catch (Exception x) {
			System.out.println("failed to start Rserve process with  "
					+ x.getMessage());
			return false;
		}
		try {
			RConnection c = new RConnection();
			System.out.println("Rserve is running.");
			c.close();
			return true;
		} catch (Exception e2) {
			System.out.println("Try failed with: " + e2.getMessage());
		}
		return false;
	}

	public boolean checkLocalRserve() {
		try {
			RConnection c = new RConnection();
			System.out.println("Rserve is running.");
			c.close();
			return true;
		} catch (Exception e) {
			System.out.println("First connect try failed with:  "
					+ e.getMessage());
		}
		String opt = " CMD Rserve --no-save";
		return (
				launchRserve("R" + opt)
				|| ((new File("/usr/local/lib/R/bin/R")).exists() && launchRserve("/usr/local/lib/R/bin/R"
						+ opt))
				|| ((new File("/usr/lib/R/bin/R")).exists() && launchRserve("/usr/lib/R/bin/R"
						+ opt))
				|| ((new File("/usr/local/bin/R")).exists() && launchRserve("/usr/local/bin/R"
						+ opt))
				|| ((new File("/sw/bin/R")).exists() && launchRserve("/sw/bin/R"
						+ opt)) 
				|| ((new File(
				"/Library/Frameworks/R.framework/Resources/bin/R")).exists() && launchRserve("/Library/Frameworks/R.framework/Resources/bin/R"
				+ opt)));
		
		
	}

	public void startRServeUnterWindows() {
		if (!this.checkLocalRserve()) {

			try {
				Runtime.getRuntime().exec(
						"C:\\Programme\\R\\R-2.7.1\\bin\\Rserve.exe");
			} catch (Exception e) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Program Files\\R\\R-2.7.1\\bin\\Rserve.exe");
			} catch (Exception eee) {
			}
			
			
			
			
			try {
				Runtime.getRuntime().exec(
						"C:\\Programme\\R\\R-2.6.0\\bin\\Rserve.exe");
			} catch (Exception e) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Program Files\\R\\R-2.6.0\\bin\\Rserve.exe");
			} catch (Exception eee) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Programme\\R\\R-2.5.1\\bin\\Rserve.exe");
			} catch (Exception e) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Program Files\\R\\R-2.5.1\\bin\\Rserve.exe");
			} catch (Exception eee) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Programme\\R\\R-2.2.0\\bin\\Rserve.exe");
			} catch (Exception e) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Programme\\R\\R-2.4.1\\bin\\Rserve.exe");
			} catch (Exception e) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Program Files\\R\\R-2.4.1\\bin\\Rserve.exe");
			} catch (Exception eee) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Programme\\R\\R-2.4.0\\bin\\Rserve.exe");
			} catch (Exception e) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Program Files\\R\\R-2.4.0\\bin\\Rserve.exe");
			} catch (Exception eee) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Programme\\R\\rw2011\\bin\\Rserve.exe");
			} catch (Exception ee) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Program Files\\R\\R-2.2.0\\bin\\Rserve.exe");
			} catch (Exception eee) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Program Files\\R\\rw2011\\bin\\Rserve.exe");
			} catch (Exception eeee) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Program Files\\R\\R-2.3.1\\bin\\Rserve.exe");
			} catch (Exception eeeee) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Programme\\R\\R-2.3.1\\bin\\Rserve.exe");
			} catch (Exception eeeeee) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Programme\\R\\R-2.3.0\\bin\\Rserve.exe");
			} catch (Exception eeeeeee) {
			}

			try {
				Runtime.getRuntime().exec(
						"C:\\Program Files\\R\\R-2.3.0\\bin\\Rserve.exe");
			} catch (Exception eeeeeeee) {
			}

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
	}
}
