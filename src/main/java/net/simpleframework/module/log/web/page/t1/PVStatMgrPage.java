package net.simpleframework.module.log.web.page.t1;

import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.template.t1.T1ResizedTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/log/pvstat")
public class PVStatMgrPage extends T1ResizedTemplatePage {

	@Override
	protected TabButtons getTabButtons(final PageParameter pp) {
		return singleton(EntityDeleteLogMgrPage.class).getTabButtons(pp);
	}
}
