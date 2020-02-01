package com.project.qa.config;

import com.amazonaws.services.iot.client.*;

public class AwsIoTConfig {

    public static void test() throws AWSIotException {
        String clientEndpoint = "a1v6q64t1s5z3w-ats.iot.us-east-2.amazonaws.com";       // replace <prefix> and <region> with your own
        String clientId = "arn:aws:iot:us-east-2:347660187301:thing/qa-platform";                              // replace with your own client ID. Use unique client IDs for concurrent connections.

        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, "AKIAVB4RJO2S5IKBIZ6L", "PWVKGkyeZfGKWe6o0g0GOubPc0ce0c3ofBhSYVkj");

        client.connect();
        String topic = "my/own/topic";
        String payload = "any payload";

        client.subscribe(new AWSIotTopic(topic, AWSIotQos.QOS0) {
            @Override
            public void onMessage(AWSIotMessage message) {
                System.out.println("Msg arrived = " + message.getStringPayload() + " from topic = " + topic);
            }
        });
    }
}
