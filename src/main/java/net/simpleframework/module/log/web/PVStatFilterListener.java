package net.simpleframework.module.log.web;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.module.log.ILogContextAware;
import net.simpleframework.module.log.IPVLogService;
import net.simpleframework.module.log.PVLog;
import net.simpleframework.mvc.IFilterListener;
import net.simpleframework.mvc.IMVCConst;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PVStatFilterListener implements IFilterListener, ILogContextAware {

	class PVStat {
		int pv = 0;

		Set<String> uv = new HashSet<String>();

		Set<String> ip = new HashSet<String>();

		int averageTime = 0;

		int minTime = 0;

		int maxTime = 0;
	}

	final Map<String, PVStat> stats = new ConcurrentHashMap<String, PVStat>();

	void updateStats() {
		final IPVLogService lservice = context.getPVLogService();
		for (final Map.Entry<String, PVStat> e : stats.entrySet()) {
			final String[] arr = StringUtils.split(e.getKey(), "-");
			final PVLog log = lservice.getPVLog(Convert.toInt(arr[0]), Convert.toInt(arr[1]),
					Convert.toInt(arr[2]), Convert.toInt(arr[3]));
			final PVStat _stat = e.getValue();
			log.setPv(log.getPv() + _stat.pv);
			log.setUv(log.getUv() + _stat.uv.size());
			log.setIp(log.getIp() + _stat.ip.size());
			if (_stat.pv > 1) {
				log.setAverageTime(_stat.averageTime / (_stat.pv - 1));
				log.setMinTime(_stat.minTime);
				log.setMaxTime(_stat.maxTime);
			}
			lservice.update(log);
		}
		stats.clear();
	}

	@Override
	public EFilterResult doFilter(final PageRequestResponse rRequest, final FilterChain filterChain)
			throws IOException {
		if (rRequest.isHttpRequest()) {
			final String dk = Convert.toDateString(new Date(), "yyyy-MM-dd-HH");
			PVStat stat = stats.get(dk);
			if (stat == null) {
				updateStats();
				stats.put(dk, stat = new PVStat());
			}

			stat.pv++;
			stat.ip.add(rRequest.getRemoteAddr());
			Object userId;
			final String sessionId = rRequest.getSessionId();
			if ((userId = rRequest.getLoginId()) != null) {
				if (!stat.uv.contains(sessionId)) {
					stat.uv.add(Convert.toString(userId));
				}
			} else {
				stat.uv.add(sessionId);
			}

			final int pt = Convert.toInt(rRequest.getSessionAttr(IMVCConst.PAGELOAD_TIME));
			stat.averageTime += pt;

			if (stat.minTime == 0 || pt < stat.minTime) {
				stat.minTime = pt;
			}
			if (stat.maxTime == 0 || pt > stat.maxTime) {
				stat.maxTime = pt;
			}
		}
		return EFilterResult.SUCCESS;
	}
}
