package cn.cc1w.app.ui.utils;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Dns;

/**
 * @author kpinfo
 */
public class MyDnsUtils implements Dns {
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (hostname == null){
            throw new UnknownHostException ("hostname == null") ;
        }else{
            try{
                List<InetAddress> inetAddressList = new ArrayList<>();
                InetAddress[] inetAddresses = InetAddress.getAllByName (hostname) ;
                for (InetAddress inetAddress: inetAddresses){
                    if (inetAddress instanceof Inet4Address){
                        inetAddressList.add(0, inetAddress) ;
                    }else{
                        inetAddressList.add(inetAddress);
                    }
                }
                return inetAddressList;
            }catch (NullPointerException var4){
                UnknownHostException unknownHostException = new UnknownHostException("Broken system behavior");
                unknownHostException.initCause(var4);
                throw unknownHostException;
            }
        }
    }
}