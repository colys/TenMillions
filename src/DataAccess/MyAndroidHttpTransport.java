package DataAccess;
 

import java.io.IOException;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

/**
 * �̳���HttpTransportSE ,�������ʱ�Ĳ���
 * 
 * @author wsx
 * 
 */
public class MyAndroidHttpTransport extends HttpTransportSE {

    private int timeout = 30000; // Ĭ�ϳ�ʱʱ��Ϊ30s

    public MyAndroidHttpTransport(String url) {
        super(url);
    }

    public MyAndroidHttpTransport(String url, int timeout) {
        super(url);
        this.timeout = timeout;
    }

    @Override
    protected ServiceConnection getServiceConnection() throws IOException {
        ServiceConnectionSE serviceConnection = new ServiceConnectionSE(url);
        serviceConnection.setConnectionTimeOut(timeout);

        return serviceConnection;
    }
}