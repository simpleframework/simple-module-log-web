package net.simpleframework.module.log.web;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.IApplicationContext;
import net.simpleframework.ctx.Module;
import net.simpleframework.ctx.ModuleFunctions;
import net.simpleframework.module.log.impl.LogContext;
import net.simpleframework.module.log.web.page.t1.LoginLogMgrPage;
import net.simpleframework.mvc.IMVCContextVar;
import net.simpleframework.mvc.ctx.WebModuleFunction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class LogWebContext extends LogContext implements IMVCContextVar {

	@Override
	public void onInit(final IApplicationContext application) throws Exception {
		super.onInit(application);

		mvcContext.addFilterListener(sListener = new PVStatFilterListener());
	}

	private PVStatFilterListener sListener;

	@Override
	public void onShutdown(final IApplicationContext application) throws Exception {
		super.onShutdown(application);
		if (sListener != null) {
			sListener.updateStats();
		}
	}

	@Override
	protected Module createModule() {
		return super.createModule().setDefaultFunction(FUNC_LOGINLOG_MGR);
	}

	@Override
	protected ModuleFunctions getFunctions() {
		return ModuleFunctions.of(FUNC_LOGINLOG_MGR);
	}

	public static WebModuleFunction FUNC_LOGINLOG_MGR = (WebModuleFunction) new WebModuleFunction(
			LoginLogMgrPage.class).setName(MODULE_NAME + "-LoginLogMgrPage").setText(
			$m("LogContext.0"));
}
