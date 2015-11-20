/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package piePlateITs;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.ZeroconfigDiscoveryListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.ZeroconfigDiscoveryService;
import org.pieShare.pieTools.pieUtilities.service.networkService.NetworkService;
import org.testng.annotations.Test;
import javax.jmdns.ServiceListener;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ZeroconfDiscoveryIT {

	private boolean shutdown = false;
	@Test
	public void testSimpleDiscovery() throws Exception {
		NetworkService service = new NetworkService();
		ZeroconfigDiscoveryService discovery1 = new ZeroconfigDiscoveryService(service, "test1");
		ZeroconfigDiscoveryListener listener1 = new ZeroconfigDiscoveryListener();
		discovery1.setListener(listener1);

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				ZeroconfigDiscoveryService discovery2 = new ZeroconfigDiscoveryService(service, "test2");
				ZeroconfigDiscoveryListener listener2 = new ZeroconfigDiscoveryListener();
				discovery2.setListener(listener2);
				while (!shutdown) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
					}
				}
			}
		});
		t.start();
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				ZeroconfigDiscoveryService discovery3 = new ZeroconfigDiscoveryService(service, "test3");
				ZeroconfigDiscoveryListener listener3 = new ZeroconfigDiscoveryListener();
				discovery3.setListener(listener3);
				while (!shutdown) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
					}
				}
			}
		});
		t2.start();

		discovery1.registerService(service.getAvailablePort());
		shutdown = true;
	}
}
