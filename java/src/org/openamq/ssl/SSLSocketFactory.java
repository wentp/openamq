package org.openamq.ssl;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

/**
 * Simple Socket factory to create sockets with or without SSL enabled.
 * If SSL enabled a "bogus" SSL Context is used (suitable for test purposes).
 * <p/>
 * This is based on an example that comes with MINA, written by Trustin Lee.
 */
public class SSLSocketFactory extends SocketFactory
{
    private static boolean sslEnabled = false;

    private static javax.net.ssl.SSLSocketFactory sslFactory = null;

    private static javax.net.SocketFactory factory = null;

    public SSLSocketFactory()
    {
        super();
    }

    public Socket createSocket(String arg1, int arg2) throws IOException,
            UnknownHostException
    {
        if (isSslEnabled())
        {
            return getSSLFactory().createSocket(arg1, arg2);
        }
        else
        {
            return new Socket(arg1, arg2);
        }
    }

    public Socket createSocket(String arg1, int arg2, InetAddress arg3,
                               int arg4) throws IOException,
            UnknownHostException
    {
        if (isSslEnabled())
        {
            return getSSLFactory().createSocket(arg1, arg2, arg3, arg4);
        }
        else
        {
            return new Socket(arg1, arg2, arg3, arg4);
        }
    }

    public Socket createSocket(InetAddress arg1, int arg2)
            throws IOException
    {
        if (isSslEnabled())
        {
            return getSSLFactory().createSocket(arg1, arg2);
        }
        else
        {
            return new Socket(arg1, arg2);
        }
    }

    public Socket createSocket(InetAddress arg1, int arg2, InetAddress arg3,
                               int arg4) throws IOException
    {
        if (isSslEnabled())
        {
            return getSSLFactory().createSocket(arg1, arg2, arg3, arg4);
        }
        else
        {
            return new Socket(arg1, arg2, arg3, arg4);
        }
    }

    public static javax.net.SocketFactory getSocketFactory()
    {
        if (factory == null)
        {
            factory = new SSLSocketFactory();
        }
        return factory;
    }

    private javax.net.ssl.SSLSocketFactory getSSLFactory()
    {
        if (sslFactory == null)
        {
            try
            {
                sslFactory = BogusSSLContextFactory.getInstance(false)
                        .getSocketFactory();
            }
            catch (GeneralSecurityException e)
            {
                throw new RuntimeException("could not create SSL socket", e);
            }
        }
        return sslFactory;
    }

    public static boolean isSslEnabled()
    {
        return sslEnabled;
    }

    public static void setSslEnabled(boolean newSslEnabled)
    {
        sslEnabled = newSslEnabled;
    }

}