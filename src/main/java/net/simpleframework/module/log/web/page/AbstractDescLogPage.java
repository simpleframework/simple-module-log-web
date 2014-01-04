package net.simpleframework.module.log.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.mvc.component.base.validation.EValidatorMethod;
import net.simpleframework.mvc.component.base.validation.Validator;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractDescLogPage extends FormTableRowTemplatePage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addFormValidationBean(pp).addValidators(
				new Validator(EValidatorMethod.required, "#sl_description"));
	}

	protected InputElement createTextarea(final PageParameter pp) {
		return InputElement.textarea("sl_description").setRows(6);
	}

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		return TableRows.of(new TableRow(new RowField($m("AbstractDescLogPage.0"), createTextarea(pp))));
	}
}
