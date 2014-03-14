package net.simpleframework.module.log.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.simpleframework.ado.bean.IIdBeanAware;
import net.simpleframework.ado.db.DbTableColumn;
import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.log.EntityUpdateLog;
import net.simpleframework.module.log.ILogContext;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.GroupWrapper;
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumns;
import net.simpleframework.mvc.component.ui.pager.db.GroupDbTablePagerHandler;
import net.simpleframework.mvc.template.TemplateUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class EntityUpdateLogPage extends AbstractLogPage {

	public static final String COL_VALNAME = "valName";
	public static final String COL_FROMVAL = "fromVal";
	public static final String COL_TOVAL = "toVal";
	public static final String COL_OPID = "opId";

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addTablePagerBean(pp, "EntityUpdateLogPage_tbl", EntityFieldTable.class).setShowCheckbox(
				isRoleMember(pp));

		// delete
		addDeleteAjaxRequest(pp, "EntityUpdateLogPage_delete");
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp, final String name,
			final Class<? extends ITablePagerHandler> tHdl) {
		final TablePagerBean tablePager = (TablePagerBean) super.addTablePagerBean(pp, name, tHdl)
				.setDetailField(TablePagerColumn.DESCRIPTION).setPageItems(50);
		tablePager.addColumn(setColumnProperties(newColumn(COL_VALNAME)))
				.addColumn(setColumnProperties(newColumn(COL_USERTEXT)))
				.addColumn(setColumnProperties(newColumn(COL_CREATEDATE)))
				.addColumn(setColumnProperties(newColumn(COL_FROMVAL)))
				.addColumn(setColumnProperties(newColumn(COL_TOVAL)))
				.addColumn(setColumnProperties(newColumn(COL_IP)));
		if (isRoleMember(pp)) {
			tablePager.addColumn(TablePagerColumn.OPE().setWidth(60));
		}
		return tablePager;
	}

	protected TablePagerColumn setColumnProperties(final TablePagerColumn col) {
		final String name = col.getColumnName();
		if (COL_VALNAME.equals(name)) {
			col.setColumnText($m("EntityUpdateLogPage.1")).setWidth(100).setTextAlign(ETextAlign.left)
					.setFilter(false);
		} else if (COL_USERTEXT.equals(name)) {
			col.setColumnText($m("EntityUpdateLogPage.4")).setWidth(100).setTextAlign(ETextAlign.left);
		} else if (COL_CREATEDATE.equals(name)) {
			col.setColumnText($m("EntityUpdateLogPage.6")).setWidth(115).setPropertyClass(Date.class);
		} else if (COL_FROMVAL.equals(name)) {
			col.setColumnText($m("EntityUpdateLogPage.2"));
		} else if (COL_TOVAL.equals(name)) {
			col.setColumnText($m("EntityUpdateLogPage.3"));
		} else if (COL_IP.equals(name)) {
			col.setColumnText($m("EntityUpdateLogPage.5")).setWidth(110);
		}
		return col;
	}

	protected TablePagerColumn newColumn(final String name) {
		return new TablePagerColumn(name);
	}

	@Transaction(context = ILogContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("id"));
		if (ids != null) {
			logContext.getEntityUpdateLogService().delete(ids);
		}
		return new JavascriptForward("$Actions['EntityUpdateLogPage_tbl']();");
	}

	protected String convertVal(final String valName, final DbTableColumn oCol, final String oVal) {
		if (oVal == null) {
			return "[NULL]";
		}
		final Class<?> colClass = oCol.getPropertyClass();
		if (boolean.class.isAssignableFrom(colClass) || Boolean.class.isAssignableFrom(colClass)) {
			return Convert.toBool(oVal) ? $m("EntityUpdateLogPage.9") : $m("EntityUpdateLogPage.10");
		} else if (Enum.class.isAssignableFrom(colClass)) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			final Enum e = Convert.toEnum((Class<Enum>) colClass, oVal);
			if (e != null) {
				return e.toString();
			}
		}
		return oVal;
	}

	protected Map<String, Object> getRowData(final ComponentParameter cp, final EntityUpdateLog field) {
		final KVMap kv = new KVMap();
		final String valName = field.getValName();
		final DbTableColumn col = getTableColumns(cp, valName);
		kv.put(COL_VALNAME, col != null ? col.getText() : valName);
		final String fromVal = field.getFromVal(), toVal = field.getToVal();
		kv.put(COL_FROMVAL, new SpanElement(convertVal(valName, col, fromVal)).setColor("#700"));
		kv.put(COL_TOVAL, new SpanElement(convertVal(valName, col, toVal)).setColor("#070"));
		kv.put(COL_USERTEXT, TemplateUtils.toIconUser(cp, field.getUserId(), field.getUserText()));
		kv.put(COL_CREATEDATE, field.getCreateDate());
		kv.put(COL_IP, field.getIp());
		final String desc = field.getDescription();
		if (StringUtils.hasText(desc)) {
			kv.put(TablePagerColumn.DESCRIPTION,
					new SpanElement(desc).addStyle("color: #666; font-size: 9.5pt;"));
		}

		final StringBuilder sb = new StringBuilder();
		sb.append(ButtonElement.deleteBtn().setOnclick(
				"$Actions['EntityUpdateLogPage_delete']('id=" + field.getId() + "');"));
		kv.add(TablePagerColumn.OPE, sb.toString());
		return kv;
	}

	protected Object convertGroupVal(final PageParameter pp, final String g, final Object groupVal) {
		if (COL_VALNAME.equals(g)) {
			final DbTableColumn oCol = getTableColumns(pp, (String) groupVal);
			if (oCol != null) {
				return new SpanElement(oCol.getText()).setTitle(Convert.toString(groupVal));
			}
		} else if (COL_OPID.equals(g)) {
			return opIdCache.get(groupVal);
		}
		return groupVal;
	}

	private static final String BEAN_TABLE_COLUMNs = "BEAN_TABLE_COLUMNs";

	@SuppressWarnings("unchecked")
	protected DbTableColumn getTableColumns(final PageParameter pp, final String valName) {
		Map<String, DbTableColumn> cols = (Map<String, DbTableColumn>) pp
				.getRequestAttr(BEAN_TABLE_COLUMNs);
		if (cols == null) {
			final Object o = getBean(pp);
			if (o instanceof IIdBeanAware) {
				pp.setRequestAttr(BEAN_TABLE_COLUMNs,
						cols = DbTableColumn.getTableColumns(o.getClass()));
			}
		}
		return cols != null ? cols.get(valName) : null;
	}

	@Override
	public String getTitle(final PageParameter pp) {
		return $m("Button.Log") + " - " + getBean(pp);
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of(LinkButton.closeBtn());
		if (isRoleMember(pp)) {
			el.append(
					SpanElement.SPACE,
					LinkButton.deleteBtn().setOnclick(
							"$Actions['EntityUpdateLogPage_tbl'].doAct('EntityUpdateLogPage_delete');"));
		}
		return el;
	}

	private static Option OPTION_1 = new Option(COL_VALNAME, $m("EntityUpdateLogPage.7"));
	private static Option OPTION_2 = new Option(COL_OPID, $m("EntityUpdateLogPage.8"));

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		pp.putParameter(G, COL_OPID);

		return ElementList.of(createGroupElement(pp, "EntityUpdateLogPage_tbl", OPTION_1, OPTION_2));
	}

	protected static final Map<Object, Object> opIdCache = new HashMap<Object, Object>();

	public static class EntityFieldTable extends GroupDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final EntityUpdateLogPage page = get(cp);
			final Object bean = page.getBean(cp);
			if (bean == null) {
				return DataQueryUtils.nullQuery();
			}
			cp.addFormParameter(page.getBeanIdParameter(cp),
					bean instanceof IIdBeanAware ? ((IIdBeanAware) bean).getId() : bean);
			return logContext.getEntityUpdateLogService().queryLog(bean);
		}

		@Override
		public GroupWrapper getGroupWrapper(final ComponentParameter cp, final Object groupVal) {
			final Object groupVal2 = ((EntityUpdateLogPage) get(cp)).convertGroupVal(cp,
					cp.getParameter(G), groupVal);
			return super.getGroupWrapper(cp, groupVal2 != null ? groupVal2 : groupVal);
		}

		@Override
		public Object getGroupValue(final ComponentParameter cp, final Object bean,
				final String groupColumn) {
			final Object groupVal = super.getGroupValue(cp, bean, groupColumn);
			if (COL_OPID.equals(groupColumn)) {
				opIdCache.put(groupVal, ((EntityUpdateLog) bean).getCreateDate());
			}
			return groupVal;
		}

		@Override
		public AbstractTablePagerSchema createTablePagerSchema() {
			return new DefaultDbTablePagerSchema() {
				@Override
				public TablePagerColumns getTablePagerColumns(final ComponentParameter cp) {
					final TablePagerColumns columns = super.getTablePagerColumns(cp);
					final String g = cp.getParameter(G);
					columns.get(COL_VALNAME).setVisible(!COL_VALNAME.equals(g));
					columns.get(COL_CREATEDATE).setVisible(!COL_OPID.equals(g));
					return columns;
				}

				@Override
				public Map<String, Object> getRowData(final ComponentParameter cp,
						final Object dataObject) {
					return ((EntityUpdateLogPage) get(cp)).getRowData(cp, (EntityUpdateLog) dataObject);
				}
			};
		}
	}
}