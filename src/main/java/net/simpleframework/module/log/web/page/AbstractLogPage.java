package net.simpleframework.module.log.web.page;

import net.simpleframework.ctx.InjectCtx;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;
import net.simpleframework.module.log.ILogContext;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.lets.OneTableTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractLogPage extends OneTableTemplatePage implements ILogConst {

	@InjectCtx
	protected static ILogContext logContext;

	/* 子类定义监听的实体Bean对象 */
	protected Object getBean(final PageParameter pp) {
		final IDbBeanService<?> beanService = getBeanService();
		if (beanService == null) {
			return null;
		}
		return getCacheBean(pp, beanService, getBeanIdParameter(pp));
	}

	@Override
	public String getRole(final PageParameter pp) {
		return IPermissionConst.ROLE_MANAGER;
	}

	protected IDbBeanService<?> getBeanService() {
		return null;
	}

	public String getBeanIdParameter(final PageParameter pp) {
		return "beanId";
	}
}