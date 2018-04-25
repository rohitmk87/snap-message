package com.jcp.omni.snap.util;

import org.springframework.stereotype.Service;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.policies.DefaultRetryPolicy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class CassandraConnection {
  public com.datastax.driver.core.Session createConnection(String cassNode1, String jcp_account)
      throws UnknownHostException {
    Cluster cluster = null;
    com.datastax.driver.core.Session session;
    InetAddress node1;

    node1 = InetAddress.getByName(cassNode1);

    Collection<InetAddress> addressess = new ArrayList<InetAddress>();
    addressess.add(node1);

    cluster = Cluster
        .builder()
        .addContactPoints(addressess)
        .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
        .build();
    session = cluster.connect(jcp_account);
    return session;
  }
}
