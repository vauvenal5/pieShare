/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package piePlateITs;

import java.util.List;
import javax.inject.Provider;
import javax.jmdns.ServiceEvent;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.ZeroconfigDiscoveryListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.ZeroconfigDiscoveryService;
import org.pieShare.pieTools.pieUtilities.service.networkService.NetworkService;
import org.testng.annotations.Test;
import javax.jmdns.ServiceListener;
import org.junit.Assert;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.IMemberDiscoveredListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.MemberDiscoveredEvent;
import org.pieShare.pieTools.pieUtilities.service.eventBase.EventBase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ZeroconfDiscoveryIT {

	private DiscoveredMember member;
	ZeroconfigDiscoveryService discovery2;
	ZeroconfigDiscoveryService discovery1;

	private NetworkService service = new NetworkService();

	@BeforeMethod
	protected void beforeTest() {
		member = null;
	}

	@AfterMethod
	protected void afterTest() {
		discovery2.shutdown();
		discovery1.shutdown();
	}

	@Test(timeOut = 60000)
	public void testListDiscovery() throws Exception {
		discovery2 = new ZeroconfigDiscoveryService();
		discovery2.setNetworkService(service);
		discovery2.setListener(new ServiceListener() {
			@Override
			public void serviceAdded(ServiceEvent event) {
				//ignore
			}

			@Override
			public void serviceRemoved(ServiceEvent event) {
				//ignore
			}

			@Override
			public void serviceResolved(ServiceEvent event) {
				//ignore
			}
		});
		discovery2.registerService("mycloud", service.getAvailablePort());

		ZeroconfigDiscoveryService discovery3 = new ZeroconfigDiscoveryService();
		discovery3.setNetworkService(service);
		discovery3.setListener(new ServiceListener() {
			@Override
			public void serviceAdded(ServiceEvent event) {
				//ignore
			}

			@Override
			public void serviceRemoved(ServiceEvent event) {
				//ignore
			}

			@Override
			public void serviceResolved(ServiceEvent event) {
				//ignore
			}
		});
		discovery3.registerService("mycloud", service.getAvailablePort());

		discovery1 = new ZeroconfigDiscoveryService();
		discovery1.setNetworkService(service);
		discovery1.setListener(new ServiceListener() {
			@Override
			public void serviceAdded(ServiceEvent event) {
				//ignore
			}

			@Override
			public void serviceRemoved(ServiceEvent event) {
				//ignore
			}

			@Override
			public void serviceResolved(ServiceEvent event) {
				//ignore
			}
		});
		
		discovery1.setDiscoveredMemberProvider(new Provider<DiscoveredMember>() {
			@Override
			public DiscoveredMember get() {
				return new DiscoveredMember();
			}
		});

		List<DiscoveredMember> members = discovery1.list("mycloud");
		Assert.assertEquals(2, members.size());

		discovery1.registerService("mycloud", service.getAvailablePort());

		members = discovery1.list("mycloud");
		Assert.assertEquals(2, members.size());

		discovery3.shutdown();

		//shutdown needs a little bit time to clean up
		Thread.sleep(1000);

		members = discovery1.list("mycloud");
		Assert.assertEquals(1, members.size());
	}

	@Test(timeOut = 60000)
	public void testDiscoveryListener() throws Exception {

		discovery2 = new ZeroconfigDiscoveryService();
		discovery2.setNetworkService(service);

		ZeroconfigDiscoveryListener listener = new ZeroconfigDiscoveryListener();
		listener.setMemberDiscoveredEventBase(new EventBase<>());
		listener.getMemberDiscoveredEventBase().addEventListener(new IMemberDiscoveredListener() {
			@Override
			public void handleObject(MemberDiscoveredEvent event) {
				member = event.getMember();
			}
		});
		listener.setDiscoveredMemberProvider(new Provider() {
			@Override
			public Object get() {
				return new DiscoveredMember();
			}
		});

		discovery2.setListener(listener);
		discovery2.registerService("mycloud", service.getAvailablePort());

		discovery1 = new ZeroconfigDiscoveryService();
		discovery1.setNetworkService(service);
		discovery1.setListener(new ServiceListener() {
			@Override
			public void serviceAdded(ServiceEvent event) {
				//ignore
			}

			@Override
			public void serviceRemoved(ServiceEvent event) {
				//ignore
			}

			@Override
			public void serviceResolved(ServiceEvent event) {
				//ignore
			}
		});
		discovery1.registerService("mycloud", 7777);

		while (member == null) {
			Thread.sleep(500);
		}

		Assert.assertEquals(7777, member.getPort());
		Assert.assertEquals(service.getLocalHost().getHostAddress(), member.getInetAdresses().getHostAddress());
	}
}
