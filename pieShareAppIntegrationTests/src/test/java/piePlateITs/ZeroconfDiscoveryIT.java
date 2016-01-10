/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package piePlateITs;

import java.util.List;
import java.util.UUID;
import javax.inject.Provider;
import javax.jmdns.ServiceEvent;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.ZeroconfigDiscoveryListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.ZeroconfigDiscoveryService;
import org.pieShare.pieTools.pieUtilities.service.networkService.NetworkService;
import org.testng.annotations.Test;
import javax.jmdns.ServiceListener;
import org.junit.Assert;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.IJmdnsDiscoveryListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.IMemberDiscoveredListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.MemberDiscoveredEvent;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.MemberEvent;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.MemberRemovedEvent;
import org.pieShare.pieTools.pieUtilities.service.eventBase.EventBase;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
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
		String clusterName = UUID.randomUUID().toString();
		discovery2 = new ZeroconfigDiscoveryService();
		discovery2.setNetworkService(service);
		discovery2.setListener(new IJmdnsDiscoveryListener() {
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

			@Override
			public IEventBase<IMemberDiscoveredListener, MemberEvent> getMemberDiscoveredEventBase() {
				throw new UnsupportedOperationException("Not supported yet."); //ignore
			}

			@Override
			public void setMyself(String myself) {
				//ignore
			}

			@Override
			public void setDiscoveryService(ZeroconfigDiscoveryService discoveryService) {
				//ignore
			}

			@Override
			public void setCloudName(String cloudName) {
				//ignore
			}
		});
		discovery2.registerService(clusterName, service.getAvailablePort());

		ZeroconfigDiscoveryService discovery3 = new ZeroconfigDiscoveryService();
		discovery3.setNetworkService(service);
		discovery3.setListener(new IJmdnsDiscoveryListener() {
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
			
			@Override
			public IEventBase<IMemberDiscoveredListener, MemberEvent> getMemberDiscoveredEventBase() {
				throw new UnsupportedOperationException("Not supported yet."); //ignore
			}

			@Override
			public void setMyself(String myself) {
				//ignore
			}

			@Override
			public void setDiscoveryService(ZeroconfigDiscoveryService discoveryService) {
				//ignore
			}
			
			@Override
			public void setCloudName(String cloudName) {
				//ignore
			}
		});
		discovery3.registerService(clusterName, service.getAvailablePort());

		discovery1 = new ZeroconfigDiscoveryService();
		discovery1.setNetworkService(service);
		discovery1.setListener(new IJmdnsDiscoveryListener() {
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
			
			@Override
			public IEventBase<IMemberDiscoveredListener, MemberEvent> getMemberDiscoveredEventBase() {
				throw new UnsupportedOperationException("Not supported yet."); //ignore
			}

			@Override
			public void setMyself(String myself) {
				//ignore
			}

			@Override
			public void setDiscoveryService(ZeroconfigDiscoveryService discoveryService) {
				//ignore
			}
			
			@Override
			public void setCloudName(String cloudName) {
				//ignore
			}
		});
		
		discovery1.setDiscoveredMemberProvider(new Provider<DiscoveredMember>() {
			@Override
			public DiscoveredMember get() {
				return new DiscoveredMember();
			}
		});

		discovery1.registerService(clusterName, service.getAvailablePort());

		List<DiscoveredMember> members = discovery1.list();
		Assert.assertEquals(2, members.size());

		discovery3.shutdown();

		//shutdown needs a little bit time to clean up
		Thread.sleep(1000);

		members = discovery1.list();
		Assert.assertEquals(1, members.size());
	}

	@Test(timeOut = 60000)
	public void testDiscoveryListener() throws Exception {
		String clusterName = UUID.randomUUID().toString();
		discovery2 = new ZeroconfigDiscoveryService();
		discovery2.setNetworkService(service);

		ZeroconfigDiscoveryListener listener = new ZeroconfigDiscoveryListener();
		listener.setMemberDiscoveredEventBase(new EventBase<>());
		listener.getMemberDiscoveredEventBase().addEventListener(new IMemberDiscoveredListener() {
			@Override
			public void handleObject(MemberEvent event) {
				member = event.getMember();
			}

			@Override
			public void handleRemoveMember(MemberRemovedEvent event) {
				//ignore
			}
		});
		listener.setDiscoveredMemberProvider(new Provider() {
			@Override
			public Object get() {
				return new DiscoveredMember();
			}
		});

		discovery2.setListener(listener);
		discovery2.registerService(clusterName, service.getAvailablePort());

		discovery1 = new ZeroconfigDiscoveryService();
		discovery1.setNetworkService(service);
		discovery1.setListener(new IJmdnsDiscoveryListener() {
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
			
			@Override
			public IEventBase<IMemberDiscoveredListener, MemberEvent> getMemberDiscoveredEventBase() {
				throw new UnsupportedOperationException("Not supported yet."); //ignore
			}

			@Override
			public void setMyself(String myself) {
				//ignore
			}

			@Override
			public void setDiscoveryService(ZeroconfigDiscoveryService discoveryService) {
				//ignore
			}
			
			@Override
			public void setCloudName(String cloudName) {
				//ignore
			}
		});
		discovery1.registerService(clusterName, 7777);

		while (member == null) {
			Thread.sleep(500);
		}

		Assert.assertEquals(7777, member.getPort());
		Assert.assertEquals(service.getLocalHost().getHostAddress(), member.getInetAdresses().getHostAddress());
	}
}
