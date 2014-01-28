package net.simpleframework.module.log.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.module.log.ILogContextAware;
import net.simpleframework.module.log.PVLog;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.CalendarInput;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ext.highchart.DataObj;
import net.simpleframework.mvc.component.ext.highchart.HcChart;
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
		pp.addImportCSS(PVStatMgrPage.class, "/log.css");

		final HighchartBean hc1 = (HighchartBean) addHighchartBean(pp, "PVStatMgrPage_lchart")
				.setTitle($m("PVStatMgrPage.4")).setContainerId("idPVStatMgrPage_lchart");
		hc1.setxAxis(new HcXAxis());
		hc1.setyAxis(new HcYAxis().setTitle($m("PVStatMgrPage.2")));
		hc1.setTooltip(new HcTooltip().setUseHTML(true));

		final HighchartBean hc2 = (HighchartBean) addHighchartBean(pp, "PVStatMgrPage_lchart2")
				.setTitle($m("PVStatMgrPage.5")).setContainerId("idPVStatMgrPage_lchart2");
		hc2.setxAxis(new HcXAxis());
		hc2.setyAxis(new HcYAxis().setTitle($m("PVStatMgrPage.2") + " (ms)"));

		final boolean d = _isDay(pp);
		final boolean m = _isMonth(pp);
		Map<Integer, PVLog> data = null;
		int count = 0;
		int start = 0;
		String title;
		if (d) {
			final Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, _getYear(pp));
			cal.set(Calendar.MONTH, _getMonth(pp));
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			count = cal.get(Calendar.DAY_OF_MONTH);
			title = $m("PVStatMgrPage.6");
		} else if (m) {
			count = 12;
			title = $m("PVStatMgrPage.7");
		} else {
			count = 23;
			start = -1;
			title = $m("PVStatMgrPage.1");
		}

		final List<String> l = new ArrayList<String>();
		for (int i = start; i < count; i++) {
			l.add(String.valueOf(i + 1));
		}

		final String[] categories = l.toArray(new String[l.size()]);
		hc1.getxAxis().setCategories(categories).setTitle(title);
		hc2.getxAxis().setCategories(categories).setTitle(title);

		hc1.getTooltip().setHeaderFormat("{point.key}<br/>");

		if (d) {
			data = context.getPVLogService().getDayStat(_getYear(pp), _getMonth(pp));
		} else if (m) {
			data = context.getPVLogService().getMonthStat(_getYear(pp));
		} else {
			addCalendarBean(pp, "PVStatMgrPage_cal").setClearButton(false)
					.setCloseCallback(
							"$Actions.loc('" + url(PVStatMgrPage.class)
									+ "?date=' + date.format('yyyy-MM-dd'))");

			final String[] arr = StringUtils.split(Convert.toDateString(_getDate(pp), "yyyy-MM-dd"),
					"-");
			data = context.getPVLogService().getHourStat(Convert.toInt(arr[0]), Convert.toInt(arr[1]),
					Convert.toInt(arr[2]));
		}

		if (data != null) {
			hc1.addSeries(getHourSeries(data, count, start, "PV", "pv"));
			hc1.addSeries(getHourSeries(data, count, start, "UV", "uv"));
			hc1.addSeries(getHourSeries(data, count, start, $m("PVStatMgrPage.3"), "ip"));

			hc2.addSeries(getHourSeries(data, count, start, $m("PVStatMgrPage.8"), "averageTime"));
			hc2.addSeries(getHourSeries(data, count, start, $m("PVStatMgrPage.9"), "minTime"));
			hc2.addSeries(getHourSeries(data, count, start, $m("PVStatMgrPage.10"), "maxTime"));
		}
	}

	private HcSeries getHourSeries(final Map<Integer, PVLog> data, final int count, final int start,
			final String name, final String property) {
		final HcSeries h = new HcSeries().setName(name);
		for (int i = start; i < count; i++) {
			final int k = i + 1;
			final PVLog log = data.get(k);
			h.addData(new DataObj(String.valueOf(k), log == null ? 0 : (Number) BeanUtils.getProperty(
					log, property)));
		}
		return h;
	}

	private HighchartBean addHighchartBean(final PageParameter pp, final String name) {
		final HighchartBean hc = addComponentBean(pp, name, HighchartBean.class);
		hc.setChart(new HcChart().setHeight(420).setMarginTop(40).setMarginRight(30));
		return hc;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of();
		final boolean d = _isDay(pp);
		final boolean m = _isMonth(pp);
		final boolean y = d || m;
		if (y) {
			final InputElement ySelect = InputElement.select();
			final int _year = _getYear(pp);
			String url = url(PVStatMgrPage.class);
			final int _month = pp.getIntParameter("month");
			if (_month > 0) {
				url += "?month=" + _month;
			}
			for (int i = _year - 10; i < _year + 8; i++) {
				ySelect.addElements(new Option(i).setSelected(_year == i)).setOnchange(
						"$Actions.loc('" + url + (_month > 0 ? "&" : "?") + "year=' + $F(this));");
			}
			el.append(ySelect);
		}
		if (d) {
			final InputElement mSelect = InputElement.select();
			final int _month = _getMonth(pp);
			String url = url(PVStatMgrPage.class);
			final int _year = pp.getIntParameter("year");
			if (_year > 0) {
				url += "?year=" + _year;
			}
			for (int i = 1; i <= 12; i++) {
				mSelect.addElements(new Option(i).setSelected(_month == i)).setOnchange(
						"$Actions.loc('" + url + (_year > 0 ? "&" : "?") + "month=' + $F(this));");
			}
			el.append(SpanElement.SPACE, mSelect);
		}
		if (!y) {
			el.append(new CalendarInput("idPVStatMgrPage_cal").setCalendarComponent(
					"PVStatMgrPage_cal").setText(Convert.toDateString(_getDate(pp), "yyyy-MM-dd")));
		}
		return el;
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final boolean d = _isDay(pp);
		final boolean m = _isMonth(pp);
		final int year = _getYear(pp);
		return ElementList.of(
				new LinkButton($m("PVStatMgrPage.11")).setHref(url(PVStatMgrPage.class)).setChecked(
						!m && !d),
				new LinkButton($m("PVStatMgrPage.12")).setHref(
						url(PVStatMgrPage.class, "year=" + year + "&month=" + _getMonth(pp))).setChecked(
						d),
				new LinkButton($m("PVStatMgrPage.13"))
						.setHref(url(PVStatMgrPage.class, "year=" + year)).setChecked(m));
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return singleton(EntityDeleteLogMgrPage.class).getTabButtons(pp);
	}

	private boolean _isDay(final PageParameter pp) {
		return pp.getIntParameter("year") > 0 && pp.getIntParameter("month") > 0;
	}

	private boolean _isMonth(final PageParameter pp) {
		return pp.getIntParameter("year") > 0 && pp.getIntParameter("month") == 0;
	}

	private Date _getDate(final PageParameter pp) {
		final Date d = Convert.toDate(pp.getParameter("date"), "yyyy-MM-dd");
		return d == null ? new Date() : d;
	}

	private int _getYear(final PageParameter pp) {
		int _year = pp.getIntParameter("year");
		if (_year == 0) {
			_year = Convert.toInt(Convert.toDateString(new Date(), "yyyy"));
		}
		return _year;
	}

	private int _getMonth(final PageParameter pp) {
		int _month = pp.getIntParameter("month");
		if (_month == 0) {
			_month = Convert.toInt(Convert.toDateString(new Date(), "MM"));
		}
		return _month;
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='PVStatMgrPage'>");
		sb.append(" <div id='idPVStatMgrPage_lchart'></div>");
		sb.append(" <div id='idPVStatMgrPage_lchart2'></div>");
		sb.append("</div>");
		return sb.toString();
	}
}
