package net.simpleframework.module.log.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.bean.IIdBeanAware;
import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.log.DownloadLog;
import net.simpleframework.module.log.ILogContext;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class DownloadLogPage extends AbstractLogPage {

	public static final String COL_FILESIZE = "filesize";

	public static final String COL_FILETYPE = "filetype";

	public static final String COL_LASTUPDATE = "lastUpdate";

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, "DownloadLogPage_tbl",
				DownloadLogTable.class);
		tablePager
				.addColumn(new TablePagerColumn(COL_USERID, $m("DownloadLogPage.0"), 100))
				.addColumn(
						new TablePagerColumn(COL_CREATEDATE, $m("DownloadLogPage.1"), 115)
								.setPropertyClass(Date.class))
				.addColumn(new TablePagerColumn(COL_IP, $m("DownloadLogPage.2"), 100))
				.addColumn(
						new TablePagerColumn(COL_LASTUPDATE, $m("DownloadLogPage.5"), 115)
								.setPropertyClass(Date.class))
				// .addColumn(new TablePagerColumn(COL_FILESIZE,
				// $m("DownloadLogPage.4"), 80))
				.addColumn(TablePagerColumn.DESCRIPTION());
		if (pp.getLogin().isManager()) {
			tablePager.addColumn(TablePagerColumn.OPE().setWidth(60));
		}
		addDeleteAjaxRequest(pp, "DownloadLogPage_delete");
	}

	@Transaction(context = ILogContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("id"));
		if (ids != null) {
			logContext.getDownloadLogService().delete(ids);
		}
		return new JavascriptForward("$Actions['DownloadLogPage_tbl']();");
	}

	@Override
	public String getTitle(final PageParameter pp) {
		String title = $m("DownloadLogPage.6");
		final Object bean = getBean(pp);
		if (bean != null) {
			title += " - " + bean;
		}
		return title;
	}

	protected Map<String, Object> getTblFormParameters(final ComponentParameter cp) {
		final KVMap kv = new KVMap();
		final Object bean = getBean(cp);
		if (bean != null) {
			kv.add(getBeanIdParameter(), bean instanceof IIdBeanAware ? ((IIdBeanAware) bean).getId()
					: bean);
		}
		return kv;
	}

	public static class DownloadLogTable extends AbstractDbTablePagerHandler {

		@Override
		public Map<String, Object> getFormParameters(final ComponentParameter cp) {
			final DownloadLogPage page = get(cp);
			return ((KVMap) super.getFormParameters(cp)).addAll(page.getTblFormParameters(cp));
		}

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final DownloadLogPage page = get(cp);
			final Object bean = page.getBean(cp);
			if (bean == null) {
				return DataQueryUtils.nullQuery();
			}
			return logContext.getDownloadLogService().queryLog(bean);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final DownloadLog log = (DownloadLog) dataObject;
			final KVMap kv = new KVMap();
			final ID uId = log.getUserId();
			kv.add(COL_USERID, uId == null ? $m("DownloadLogPage.3") : cp.getUser(uId));
			kv.add(COL_CREATEDATE, log.getCreateDate());
			kv.add(COL_IP, log.getIp());
			kv.add(COL_LASTUPDATE, log.getLastUpdate());
			// kv.add(COL_FILESIZE, FileUtils.toFileSize(log.getFilesize()));
			kv.add(TablePagerColumn.DESCRIPTION, log.getDescription());
			kv.add(
					TablePagerColumn.OPE,
					ButtonElement.deleteBtn().setOnclick(
							"$Actions['DownloadLogPage_delete']('id=" + log.getId() + "');"));
			return kv;
		}
	}
}
