package net.simpleframework.module.log.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.log.ILogContext;
import net.simpleframework.module.log.ILogContextAware;
import net.simpleframework.module.log.web.page.ILogConst;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.db.GroupDbTablePagerHandler;
import net.simpleframework.mvc.template.t1.T1ResizedTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractLogMgrPage extends T1ResizedTemplatePage implements ILogConst,
		ILogContextAware {
	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		pp.addImportCSS(AbstractLogMgrPage.class, "/log.css");

		addLogComponents(pp);
	}

	protected void addLogComponents(final PageParameter pp) {
		// 删除
		addDeleteAjaxRequest(pp, "AbstractLogMgrPage_delete");
		// 用户选择
		addUserSelectForTbl(pp, "AbstractLogMgrPage_tbl");
	}

	@Override
	public String getRole(final PageParameter pp) {
		return context.getManagerRole();
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp,
			final Class<? extends LogTbl> tblClass) {
		return (TablePagerBean) addTablePagerBean(pp, "AbstractLogMgrPage_tbl").setShowLineNo(true)
				.setPageItems(50).setPagerBarLayout(EPagerBarLayout.bottom)
				.setContainerId("tbl_" + hashId).setHandlerClass(tblClass);
	}

	protected abstract IDbBeanService<?> getBeanService();

	@Transaction(context = ILogContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("id"));
		if (ids != null) {
			getBeanService().delete(ids);
		}
		return new JavascriptForward("$Actions['AbstractLogMgrPage_tbl']();");
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(LinkButton.deleteBtn().setOnclick(
				"$Actions['AbstractLogMgrPage_tbl'].doAct('AbstractLogMgrPage_delete');"));
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return TabButtons.of(new TabButton($m("AbstractLogMgrPage.1"), url(LoginLogMgrPage.class)),
				new TabButton($m("PVStatMgrPage.0"), url(PVStatMgrPage.class)));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(new SpanElement().setClassName("tabbtns").addHtml(
				TabButtons.of(new TabButton($m("LoginLogMgrPage.0"), url(LoginLogMgrPage.class)),
						new TabButton($m("EntityDeleteLogMgrPage.0"), url(EntityDeleteLogMgrPage.class)),
						new TabButton($m("DownloadLogMgrPage.0"), url(DownloadLogMgrPage.class)))
						.toString(pp)));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div align='center' class='AbstractLogMgrPage'>");
		sb.append("  <div id='tbl_").append(hashId).append("'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	static class LogTbl extends GroupDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return ((AbstractLogMgrPage) get(cp)).getBeanService().queryAll();
		}

		@Override
		public Object getGroupValue(final ComponentParameter cp, final Object bean,
				final String groupColumn) {
			if (COL_CREATEDATE.equals(groupColumn)) {
				return DateUtils.getDateCategory((Date) BeanUtils.getProperty(bean, COL_CREATEDATE));
			}
			return super.getGroupValue(cp, bean, groupColumn);
		}
	}

	protected static Option OPTION_CREATEDATE = new Option(COL_CREATEDATE,
			$m("AbstractLogMgrPage.0"));
}
