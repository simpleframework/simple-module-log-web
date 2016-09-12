package net.simpleframework.module.log.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;
import net.simpleframework.module.log.LoginLog;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.SpanElement;
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
@PageMapping(url = "/log/login/mgr")
public class LoginLogMgrPage extends AbstractLogMgrPage {

	public static final String COL_LOGOUTDATE = "logoutDate";

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, LoginLogTbl.class);
		tablePager.addColumn(new TablePagerColumn(COL_USERTEXT, $m("LoginLogMgrPage.1"), 120))
				.addColumn(TablePagerColumn.DATE(COL_CREATEDATE, $m("LoginLogMgrPage.2")))
				.addColumn(TablePagerColumn.DATE(COL_LOGOUTDATE, $m("LoginLogMgrPage.4")))
				.addColumn(new TablePagerColumn(COL_IP, $m("LoginLogMgrPage.3"), 120))
				.addColumn(TablePagerColumn.DESCRIPTION()).addColumn(TablePagerColumn.OPE(80));
	}

	@Override
	protected IDbBeanService<?> getBeanService() {
		return _logLoginService;
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		pp.putParameter(G, COL_CREATEDATE);
		return super.getRightElements(pp).append(SpanElement.SPACE,
				createGroupElement(pp, "AbstractLogMgrPage_tbl", OPTION_CREATEDATE));
	}

	public static class LoginLogTbl extends LogTbl {

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp,
				final Object dataObject) {
			final LoginLog log = (LoginLog) dataObject;
			final ID id = log.getId();
			final KVMap kv = new KVMap();
			kv.put(COL_USERTEXT, TemplateUtils.toIconUser(cp, log.getUserId(), log.getUserText()));
			kv.add(COL_CREATEDATE, log.getCreateDate());
			kv.add(COL_LOGOUTDATE, log.getLogoutDate());
			kv.add(COL_IP, log.getIp());
			kv.add(TablePagerColumn.DESCRIPTION, HtmlUtils.convertHtmlLines(log.getDescription()));
			kv.add(TablePagerColumn.OPE, ButtonElement.deleteBtn()
					.setOnclick("$Actions['AbstractLogMgrPage_delete']('id=" + id + "');"));
			return kv;
		}
	}
}
