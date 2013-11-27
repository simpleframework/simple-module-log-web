package net.simpleframework.module.log.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.module.log.ILogContextAware;
import net.simpleframework.module.log.PVLog;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ext.highchart.DataObj;
import net.simpleframework.mvc.component.ext.highchart.EChart.EAxisType;
import net.simpleframework.mvc.component.ext.highchart.EChart.EHcType;
import net.simpleframework.mvc.component.ext.highchart.HcChart;
import net.simpleframework.mvc.component.ext.highchart.HcLabels;
import net.simpleframework.mvc.component.ext.highchart.HcSeries;
import net.simpleframework.mvc.component.ext.highchart.HcTooltip;
import net.simpleframework.mvc.component.ext.highchart.HcXAxis;
import net.simpleframework.mvc.component.ext.highchart.HcYAxis;
import net.simpleframework.mvc.component.ext.highchart.HighchartBean;
import net.simpleframework.mvc.template.t1.T1ResizedTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/log/pvstat")
public class PVStatMgrPage extends T1ResizedTemplatePage implements ILogContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final HighchartBean hc = (HighchartBean) addComponentBean(pp, "PVStatMgrPage_lchart",
				HighchartBean.class).setContainerId("idPVStatMgrPage_lchart");

		hc.setChart(new HcChart().setType(EHcType.line).setMarginTop(40)).setTitle("");
		hc.setTooltip(new HcTooltip().setUseHTML(true).setHeaderFormat("{point.key}H<br/>"));

		final List<String> l = new ArrayList<String>();
		for (int i = 0; i < 24; i++) {
			l.add(i < 10 ? "0" + i : String.valueOf(i));
		}
		final HcXAxis xAxis = new HcXAxis().setCategories(l.toArray(new String[l.size()]));
		hc.setxAxis(xAxis.setTitle($m("PVStatMgrPage.1")).setType(EAxisType.linear)
				.setLabels(new HcLabels().setRotation(315)));

		hc.setyAxis(new HcYAxis().setTitle($m("PVStatMgrPage.2")));

		final String[] arr = StringUtils.split(Convert.toDateString(new Date(), "yyyy-MM-dd"), "-");
		final Map<Integer, PVLog> data = context.getPVLogService().getHourStat(Convert.toInt(arr[0]),
				Convert.toInt(arr[1]), Convert.toInt(arr[2]));

		HcSeries h = new HcSeries().setName("PV");
		for (int i = 0; i < 24; i++) {
			final PVLog log = data.get(i);
			h.addData(new DataObj(i < 10 ? "0" + i : String.valueOf(i), log == null ? 0 : log.getPv()));
		}
		hc.addSeries(h);

		h = new HcSeries().setName("UV");
		for (int i = 0; i < 24; i++) {
			final PVLog log = data.get(i);
			h.addData(new DataObj(i < 10 ? "0" + i : String.valueOf(i), log == null ? 0 : log.getUv()));
		}
		hc.addSeries(h);

		h = new HcSeries().setName($m("PVStatMgrPage.3"));
		for (int i = 0; i < 24; i++) {
			final PVLog log = data.get(i);
			h.addData(new DataObj(i < 10 ? "0" + i : String.valueOf(i), log == null ? 0 : log.getIp()));
		}
		hc.addSeries(h);
	}

	@Override
	protected TabButtons getTabButtons(final PageParameter pp) {
		return singleton(EntityDeleteLogMgrPage.class).getTabButtons(pp);
	}
}
