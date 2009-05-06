// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.n;

import jodd.proxetta.Proxetta;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.datetime.JStopWatch;
import jodd.util.ClassLoaderUtil;
import jodd.util.ReflectUtil;

import java.io.IOException;
import java.io.InputStream;

import examples.proxetta.n.proxy.Aspects;
import examples.proxetta.n.proxy.UserService;
import examples.proxetta.n.proxy.UserServiceProxy2;

public class LocalRunner {

	public static void main(String[] args) throws Exception {
		Aspects a = new Aspects();
		Proxetta p = Proxetta.withAspects(
				a.getTxAspect()
//				,a.getJoAspect()
		);

//		JStopWatch sw = new JStopWatch();
//		long l = 10000;
//		while (l-- > 0) {
//			p.createProxy(UserService.class);
//		}
//		sw.stop();
//		System.out.println(sw);         // 3.8 sekundi

		InputStream is = ClassLoaderUtil.getClassAsStream(UserServiceProxy2.class);
		byte[] b = StreamUtil.readAvailableBytes(is);
		FileUtil.writeBytes("d:\\UserServiceProxy2.class", b);
		StreamUtil.close(is);


		b = p.createProxy(UserService.class);
		if (b != null) {
			try {
				FileUtil.writeBytes("d:\\UserServiceProxy.class", b);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("----------------------------------------------------------------- RUN");

		UserService u =  p.createProxyInstance(UserService.class);
//		System.out.println(u);          --> A??????????
//		u.findUser("", "");
//		u.doo();

//		System.out.println(u.zoo(2));
		u.zoo(null, null, null);
//		u.zoo(2L);
	}
}
