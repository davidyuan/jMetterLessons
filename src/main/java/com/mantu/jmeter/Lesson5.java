package com.mantu.jmeter;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import javax.jms.*;

/**
 * blog http://www.cnblogs.com/mantu/
 *
 * @author mantu
 *
 */
public class Lesson5 extends AbstractJavaSamplerClient{
    
    public static void main(String [] args){
        
    }

    @Override
    public SampleResult runTest(JavaSamplerContext arg0) {
        // TODO Auto-generated method stub
        SampleResult sr = new SampleResult();
        sr.setSampleLabel("activemq测试");
        try{
            sr.sampleStart();
            String user = env("ACTIVEMQ_USER", "admin");
            String password = env("ACTIVEMQ_PASSWORD", "password");
            String host = env("ACTIVEMQ_HOST", "localhost");
            int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
            String destination = "event";
            int messages = 10;
            int size = 256;
            String DATA = "abcdefghijklmnopqrstuvwxyz";
            String body = "";
            for( int i=0; i < size; i ++) {
                body += DATA.charAt(i%DATA.length());
            }
    
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);
    
            Connection connection = factory.createConnection(user, password);
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = new ActiveMQQueue(destination);
            MessageProducer producer = session.createProducer(dest);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    
            for( int i=1; i <= messages; i ++) {
                TextMessage msg = session.createTextMessage(body);
                msg.setIntProperty("id", i);
                producer.send(msg);
                if( (i % 1000) == 0) {
                    System.out.println(String.format("Sent %d messages", i));
                }
            }
            producer.send(session.createTextMessage("SHUTDOWN"));
            connection.close();
            sr.setResponseData("success");
            sr.setDataType(SampleResult.TEXT);
            sr.setSuccessful(true);
        }
        catch(Exception ex){
            sr.setSuccessful(false);
            ex.printStackTrace();
        }
        finally{
            sr.sampleEnd();
        }
        return sr;
    }
    
    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if( rc== null )
            return defaultValue;
        return rc;
    }

    private static String arg(String []args, int index, String defaultValue) {
        if( index < args.length )
            return args[index];
        else
            return defaultValue;
    }
}
