package net.simpleframework.module.log.web;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.Module;
import net.simpleframework.module.log.impl.LogContext;
import net.simpleframework.module.log.web.page.t1.EntityDeleteLogMgrPage;
import net.simpleframework.mvc.ctx.WebModuleFunction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class LogWebContext extends LogContext {

	@Override
	protected Module createModule() {
		return super.createModule().setDefaultFunction(
				new WebModuleFunction(EntityDeleteLogMgrPage.class).setName(
						MODULE_NAME + "-EntityDeleteLogMgrPage").setText($m("LogContext.0")));
	}
}
