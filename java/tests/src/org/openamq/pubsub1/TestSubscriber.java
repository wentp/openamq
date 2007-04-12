package org.openamq.pubsub1;

import org.apache.log4j.Logger;
import org.openamq.client.AMQConnection;
import org.openamq.client.AMQTopic;
import org.openamq.jms.Session;

import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Topic;
import java.net.InetAddress;

/**
 * @author Robert Greig (robert.j.greig@jpmorgan.com)
 */
public class TestSubscriber
{
    private static final Logger _logger = Logger.getLogger(TestSubscriber.class);

    private static class TestMessageListener implements MessageListener
    {
        private String _name;

        private int _expectedMessageCount;

        private int _messageCount;

        private long _startTime = 0;

        public TestMessageListener(String name, int expectedMessageCount)
        {
            _name = name;
            _expectedMessageCount = expectedMessageCount;
        }

        public void onMessage(javax.jms.Message message)
        {
            if (_messageCount++ == 0)
            {
                _startTime = System.currentTimeMillis();
            }
            if (_logger.isInfoEnabled())
            {
                _logger.info(_name + " got message '" + message + "'");
            }
            if (_messageCount == _expectedMessageCount)
            {
                long totalTime = System.currentTimeMillis() - _startTime;
                _logger.error(_name + ": Total time to receive " + _messageCount + " messages was " +
                              totalTime + "ms. Rate is " + (_messageCount/(totalTime/1000.0)));
            }
            if (_messageCount > _expectedMessageCount)
            {
                _logger.error("Oops! More messages received than expected (" + _messageCount + ")");
            }
        }
    }

    public static void main(String[] args)
    {
        _logger.info("Starting...");

        if (args.length != 7)
        {
            System.out.println("Usage: host port username password virtual-path expectedMessageCount selector");
            System.exit(1);
        }
        try
        {
            InetAddress address = InetAddress.getLocalHost();
            AMQConnection con1 = new AMQConnection(args[0], Integer.parseInt(args[1]), args[2], args[3],
                                                  address.getHostName(), args[4]);
            final org.openamq.jms.Session session1 = (org.openamq.jms.Session) con1.createSession(false, Session.AUTO_ACKNOWLEDGE);

            AMQConnection con2 = new AMQConnection(args[0], Integer.parseInt(args[1]), args[2], args[3],
                                                  address.getHostName(), args[4]);
            final org.openamq.jms.Session session2 = (org.openamq.jms.Session) con2.createSession(false, Session.AUTO_ACKNOWLEDGE);
            String selector = args[6];

            final int expectedMessageCount = Integer.parseInt(args[5]);
            _logger.info("Message selector is <" + selector + ">...");

            Topic t = new AMQTopic("cbr", false);
            MessageConsumer consumer1 = session1.createConsumer(t,
                                                                100, false, false, selector);
            MessageConsumer consumer2 = session2.createConsumer(t,
                                                                100, false, false, selector);

            consumer1.setMessageListener(new TestMessageListener("ML 1", expectedMessageCount));
            consumer2.setMessageListener(new TestMessageListener("ML 2", expectedMessageCount));
            con1.start();
            con2.start();
        }
        catch (Throwable t)
        {
            System.err.println("Fatal error: " + t);
            t.printStackTrace();
        }

        System.out.println("Waiting...");
    }
}
