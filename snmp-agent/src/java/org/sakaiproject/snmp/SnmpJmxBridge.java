package org.sakaiproject.snmp;

import net.sf.snmpadaptor4j.SnmpAdaptor;

import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.net.URL;

/**
 * Created by buckett on 02/12/2014.
 */
public class SnmpJmxBridge {

    public void init() throws Exception {
        URL url = getClass().getResource("/snmp.xml");
        SnmpAdaptor adaptor = new SnmpAdaptor(url, true);

        adaptor.getSystemInfo().setSysName("MyApp");
        adaptor.getSystemInfo().setSysDescr("This is a java application");
        adaptor.getSystemInfo().setSysLocation("Web server at Montpellier (France)");
        adaptor.getSystemInfo().setSysContact("root@mydomain.fr");

        ObjectName name = new ObjectName("net.sf.snmpadaptor4j:adaptor=SnmpAdaptor");
        ManagementFactory.getPlatformMBeanServer().registerMBean(adaptor, name);

        adaptor.start();

    }
}
