package com.lightbend.akka.sample;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import jdocs.tutorial_4.DeviceManager;


public class test {
	ActorSystem system = ActorSystem.create("testSystem");
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("testSystem");
//		ActorRef first = system.actorOf(Props.create(StartStopActor1.class), "first");
//		first.tell("stop", ActorRef.noSender());		
		ActorRef supervisingActor = system.actorOf(Props.create(SupervisingActor.class), "supervising-actor");
		supervisingActor.tell("failChild", ActorRef.noSender());
	}
	

	
	@Test
	public void testReplyWithLatestTemperatureReading() {
		
	  TestKit probe = new TestKit(system);
	  ActorRef deviceActor = system.actorOf(Device.props("group", "device"));

	  deviceActor.tell(new Device.RecordTemperature(1L, 24.0), probe.getRef());
	  assertEquals(1L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);

	  deviceActor.tell(new Device.ReadTemperature(2L), probe.getRef());
	  Device.RespondTemperature response1 = probe.expectMsgClass(Device.RespondTemperature.class);
	  assertEquals(2L, response1.requestId);
	  assertEquals(Optional.of(24.0), response1.value);

	  deviceActor.tell(new Device.RecordTemperature(3L, 55.0), probe.getRef());
	  assertEquals(3L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);

	  deviceActor.tell(new Device.ReadTemperature(4L), probe.getRef());
	  Device.RespondTemperature response2 = probe.expectMsgClass(Device.RespondTemperature.class);
	 assertEquals(4L, response2.requestId);
	  assertEquals(Optional.of(55.0), response2.value);
	}
	
	
	//执行成功注册
	@Test
	public void testReplyToRegistrationRequests() {
		 
	  TestKit probe = new TestKit(system);
	  ActorRef deviceActor = system.actorOf(Device.props("group", "device"));

	  deviceActor.tell(new DeviceManager.RequestTrackDevice("group", "device"), probe.getRef());
	  probe.expectMsgClass(DeviceManager.DeviceRegistered.class);
	  assertEquals(deviceActor, probe.getLastSender());
	}

	//测试ID不匹配
	@Test
	public void testIgnoreWrongRegistrationRequests() {
		
	  TestKit probe = new TestKit(system);
	  ActorRef deviceActor = system.actorOf(Device.props("group", "device"));

	  deviceActor.tell(new DeviceManager.RequestTrackDevice("wrongGroup", "device"), probe.getRef());
	  probe.expectNoMessage();

	  deviceActor.tell(new DeviceManager.RequestTrackDevice("group", "wrongDevice"), probe.getRef());
	  probe.expectNoMessage();
	}
	
	@Test
	public void testRegisterDeviceActor() {
	  TestKit probe = new TestKit(system);
	  ActorRef groupActor = system.actorOf(DeviceGroup.props("group"));

	  groupActor.tell(new DeviceManager.RequestTrackDevice("group", "device1"), probe.getRef());
	  probe.expectMsgClass(DeviceManager.DeviceRegistered.class);
	  ActorRef deviceActor1 = probe.getLastSender();

	  groupActor.tell(new DeviceManager.RequestTrackDevice("group", "device2"), probe.getRef());
	  probe.expectMsgClass(DeviceManager.DeviceRegistered.class);
	  ActorRef deviceActor2 = probe.getLastSender();
	  assertNotEquals(deviceActor1, deviceActor2);

	  // Check that the device actors are working
	  deviceActor1.tell(new Device.RecordTemperature(0L, 1.0), probe.getRef());
	  assertEquals(0L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);
	  deviceActor2.tell(new Device.RecordTemperature(1L, 2.0), probe.getRef());
	  assertEquals(1L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);
	}

	@Test
	public void testIgnoreRequestsForWrongGroupId() {
	  TestKit probe = new TestKit(system);
	  ActorRef groupActor = system.actorOf(DeviceGroup.props("group"));

	  groupActor.tell(new DeviceManager.RequestTrackDevice("wrongGroup", "device1"), probe.getRef());
	  probe.expectNoMessage();
	}
	
}