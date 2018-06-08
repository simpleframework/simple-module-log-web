package net.simpleframework.module.log.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.common.FileUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.module.log.bean.DownloadLog;
import net.simpleframework.module.log.i.IDownloadLogService;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.template.TemplateUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/log/download/mgr")
public class DownloadLogMgrPage extends AbstractLogMgrPage {

	public static final String COL_LASTUPDATE = "lastUpdate";
	public static final String COL_FILESIZE = "filesize";
	public static final String COL_FILETYPE = "filetype";

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, DownloadLogTbl.class);
		tablePager.addColumn(new TablePagerColumn(COL_USERTEXT, $m("DownloadLogMgrPage.2"), 100))
				.addColumn(TablePagerColumn.DATE(COL_CREATEDATE, $m("DownloadLogPage.1")))
				.addColumn(TablePagerColumn.DATE(COL_LASTUPDATE, $m("DownloadLogPage.5")))
				.addColumn(new TablePagerColumn(COL_FILESIZE, $m("DownloadLogPage.4"), 70))
				.addColumn(new TablePagerColumn(COL_FILETYPE, $m("DownloadLogMgrPage.1"), 70))
				.addColumn(new TablePagerColumn(COL_IP, $m("DownloadLogPage.2"), 120))
				.addColumn(TablePagerColumn.DESCRIPTION()).addColumn(TablePagerColumn.OPE(80));
	}

	@Override
	protected IDownloadLogService getBeanService() {
		return _logDownloadService;
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		pp.putParameter(G, COL_CREATEDATE);
		return super.getRightElements(pp)
				.append(createGroupElement(pp, "AbstractLogMgrPage_tbl", OPTION_CREATEDATE));
	}

	public static class DownloadLogTbl extends LogTbl {
		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp,
				final Object dataObject) {
			final DownloadLog log = (DownloadLog) dataObject;
			final KVMap kv = new KVMap();
			kv.add(COL_USERTEXT, TemplateUtils.toIconUser(cp, log.getUserId(), log.getUserText()));
			kv.add(COL_CREATEDATE, log.getCreateDate());
			kv.add(COL_LASTUPDATE, log.getLastUpdate());
			kv.add(COL_FILESIZE, FileUtils.toFileSize(log.getFilesize()));
			kv.add(COL_FILETYPE, log.getFiletype());
			kv.add(COL_IP, log.getIp());
			kv.add(TablePagerColumn.DESCRIPTION, log.getDescription());
			kv.add(TablePagerColumn.OPE, ButtonElement.deleteBtn()
					.setOnclick("$Actions['AbstractLogMgrPage_delete']('id=" + log.getId() + "');"));
			return kv;
		}
	}
}
