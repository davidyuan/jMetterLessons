package com.mantu.jmeter;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import com.mantu.HelloWorldService;


/**
 * blog http://www.cnblogs.com/mantu/
 *
 * @author mantu
 *
 */
public class Lesson3 extends AbstractJavaSamplerClient{
    
    public static void main(String [] args){
        
    }

    @Override
    public SampleResult runTest(JavaSamplerContext arg0) {
        // TODO Auto-generated method stub
        String userName = arg0.getParameter("uName");
        SampleResult sr = new SampleResult();
        sr.setSampleLabel("thrift测试");
        try{
            sr.sampleStart();
            HelloClientDemo helloClient = new HelloClientDemo();
            helloClient.startClient(userName);
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
    
    class HelloClientDemo {

        public static final String SERVER_IP = "localhost";
        public static final int SERVER_PORT = 8090;
        public static final int TIMEOUT = 30000;

        /**
         *
         * @param userName
         */
        public void startClient(String userName) {
            TTransport transport = null;
            try {
                transport = new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT);
                TProtocol protocol = new TBinaryProtocol(transport);
                HelloWorldService.Client client = new HelloWorldService.Client(
                        protocol);
                transport.open();
                System.out.println(client.sayHello(userName));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != transport) {
                    transport.close();
                }
            }
        }
    }
}
