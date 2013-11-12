package net.simpleframework.module.log.web.page;

import net.simpleframework.ctx.InjectCtx;
import net.simpleframework.module.log.ILogContext;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.lets.OneTableTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractLogPage extends OneTableTemplatePage implements ILogConst {

	@InjectCtx
	protected static ILogContext logContext;

	/* 子类定义监听的实体Bean对象 */
	protected abstract Object getBean(PageParameter pp);

	public String getBeanIdParameter() {
		return "beanId";
	}
}