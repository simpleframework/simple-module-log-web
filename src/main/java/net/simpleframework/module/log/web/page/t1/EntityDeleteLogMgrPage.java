package net.simpleframework.module.log.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Map;

import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;
import net.simpleframework.module.log.EntityDeleteLog;
import net.simpleframework.module.log.web.page.EntityUpdateLogPage;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumns;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.mvc.template.TemplateUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/log/delete/mgr")
public class EntityDeleteLogMgrPage extends AbstractLogMgrPage {

	public static final String COL_TBLNAME = "tblName";

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, DeleteLogTbl.class);
		tablePager
				.addColumn(
						new TablePagerColumn(COL_USERTEXT, $m("EntityDeleteLogMgrPage.2"), 120)
								.setTextAlign(ETextAlign.left))
				.addColumn(
						new TablePagerColumn(COL_CREATEDATE, $m("EntityDeleteLogMgrPage.3"), 120)
								.setPropertyClass(Date.class))
				.addColumn(
						new TablePagerColumn(COL_TBLNAME, $m("EntityDeleteLogMgrPage.1"), 180)
								.setTextAlign(ETextAlign.left))
				.addColumn(new TablePagerColumn(COL_IP, $m("EntityDeleteLogMgrPage.4"), 120))
				.addColumn(TablePagerColumn.DESCRIPTION())
				.addColumn(TablePagerColumn.OPE().setWidth(140));

		// 修改日志
		addAjaxRequest(pp, "EntityDeleteLogMgrPage_logPage", _UpdateLogPage.class);
		addComponentBean(pp, "EntityDeleteLogMgrPage_logWin", WindowBean.class)
				.setContentRef("EntityDeleteLogMgrPage_logPage").setHeight(600).setWidth(960);
	}

	@Override
	protected IDbBeanService<?> getBeanService() {
		return context.getEntityDeleteLogService();
	}

	private static Option OPTION_TBLNAME = new Option(COL_TBLNAME, $m("EntityDeleteLogMgrPage.5"));

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		pp.putParameter(G, COL_CREATEDATE);
		return super.getRightElements(pp).append(SpanElement.SPACE,
				createGroupElement(pp, "AbstractLogMgrPage_tbl", OPTION_CREATEDATE, OPTION_TBLNAME));
	}

	public static class DeleteLogTbl extends LogTbl {

		@Override
		public AbstractTablePagerSchema createTablePagerSchema() {
			return new DefaultDbTablePagerSchema() {
				@Override
				public TablePagerColumns getTablePagerColumns(final ComponentParameter cp) {
					final TablePagerColumns columns = super.getTablePagerColumns(cp);
					final String g = cp.getParameter(G);
					columns.get(COL_TBLNAME).setVisible(!COL_TBLNAME.equals(g));
					// columns.get(COL_CREATEDATE).setVisible(!COL_CREATEDATE.equals(g));
					return columns;
				}

				@Override
				public Map<String, Object> getRowData(final ComponentParameter cp,
						final Object dataObject) {
					final EntityDeleteLog log = (EntityDeleteLog) dataObject;
					final ID id = log.getId();
					final KVMap kv = new KVMap();
					kv.add(COL_TBLNAME, log.getTblName());
					kv.add(COL_USERTEXT,
							TemplateUtils.toIconUser(cp, log.getUserId(), log.getUserText()));
					kv.add(COL_CREATEDATE, log.getCreateDate());
					kv.add(COL_IP, log.getIp());
					kv.add(TablePagerColumn.DESCRIPTION,
							HtmlUtils.convertHtmlLines(log.getDescription()));

					final StringBuilder sb = new StringBuilder();
					final ButtonElement logBtn = new ButtonElement($m("EntityDeleteLogMgrPage.6"));
					final ID beanId = log.getBeanId();
					if (beanId != null) {
						logBtn.setOnclick("$Actions['EntityDeleteLogMgrPage_logWin']('beanId=" + beanId
								+ "');");
					} else {
						logBtn.setDisabled(true);
					}
					sb.append(logBtn).append(SpanElement.SPACE);
					sb.append(ButtonElement.deleteBtn().setOnclick(
							"$Actions['AbstractLogMgrPage_delete']('id=" + id + "');"));
					kv.add(TablePagerColumn.OPE, sb.toString());
					return kv;
				}
			};
		}
	}

	public static class _UpdateLogPage extends EntityUpdateLogPage {

		@Override
		protected Object getBean(final PageParameter pp) {
			return ID.of(pp.getParameter("beanId"));
		}

		@Override
		public String getTitle(final PageParameter pp) {
			return super.getTitle(pp);
		}
	}
}
