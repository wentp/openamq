package org.openamq.client.protocol;

import org.apache.mina.common.Session;
import org.apache.log4j.Logger;
import org.openamq.client.AMQException;
import org.openamq.client.ConnectionTuneParameters;
import org.openamq.client.framing.Connection;
import org.openamq.client.framing.FieldTable;
import org.openamq.client.state.*;

import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Robert Greig (robert.j.greig@jpmorgan.com)
 */
public class ConnectionCloseHandler implements StateAwareFrameListener
{
    private static final Logger _logger = Logger.getLogger(ConnectionCloseHandler.class);

    private static ConnectionCloseHandler _handler = new ConnectionCloseHandler();

    public static ConnectionCloseHandler getInstance()
    {
        return _handler;
    }

    private ConnectionCloseHandler()
    {
    }

    public void frameReceived(AMQStateManager stateManager, FrameEvent evt) throws AMQException
    {
        _logger.debug("ConnectionClose frame received");
        Connection.Close frame = (Connection.Close) evt.getFrame();

        int errorCode = frame.replyCode;
        String reason = frame.replyText;

        frame.replyCode = 200;
        frame.replyText = "Client says au revoir";

        if (errorCode == 200)
        {
            stateManager.changeState(AMQState.CONNECTION_CLOSED);
        }
        else
        {
            throw new AMQException(errorCode, "Error: " + reason);
        }

        evt.getProtocolSession().writeFrame(frame);
        // this actually closes the connection
        evt.getProtocolSession().closeProtocolSession();
    }
}