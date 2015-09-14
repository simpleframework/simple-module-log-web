package net.simpleframework.module.log.web.hdl;

import java.io.File;
import java.util.Map;

import net.simpleframework.ado.bean.AbstractIdBean;
import net.simpleframework.ado.bean.IIdBeanAware;
import net.simpleframework.common.FileUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.common.bean.AttachmentFile;
import net.simpleframework.module.common.content.Attachment;
import net.simpleframework.module.common.content.IAttachmentService;
import net.simpleframework.module.common.web.content.hdl.AbstractAttachmentExHandler;
import net.simpleframework.module.log.ILogContextAware;
import net.simpleframework.module.log.web.page.DownloadLogPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.DownloadUtils;
import net.simpleframework.mvc.common.IDownloadHandler;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ext.attachments.AttachmentUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractAttachmentLogHandler<T extends Attachment, M extends AbstractIdBean>
		extends AbstractAttachmentExHandler<T, M> implements ILogContextAware {

	@SuppressWarnings("unchecked")
	@Override
	public void onDownloaded(final Object beanId, final String topic, final File oFile) {
		super.onDownloaded(beanId, topic, oFile);

		_logDownloadService.log(beanId, oFile.length(),
				FileUtils.getFilenameExtension(oFile.getName()), topic);

		// 设置下载次数
		final IAttachmentService<T> service = getAttachmentService();
		final T t = service.getBean(beanId);
		if (t != null) {
			t.setDownloads(_logDownloadService.clog(t));
			service.update(new String[] { "downloads" }, t);
		}
	}

	@Override
	public String getTooltipPath(final ComponentParameter cp) {
		return AbstractMVCPage.url(AttachmentTooltipExPage.class,
				AttachmentUtils.BEAN_ID + "=" + cp.hashId());
	}

	public static class AttachmentLogPage extends DownloadLogPage {

		@Override
		protected Map<String, Object> getTblFormParameters(final ComponentParameter cp) {
			return ((KVMap) super.getTblFormParameters(cp)).add(AttachmentUtils.BEAN_ID,
					AttachmentUtils.get(cp).hashId()).add("id", cp.getParameter("id"));
		}

		@Override
		protected IIdBeanAware getBean(final PageParameter pp) {
			return ((AbstractAttachmentLogHandler<?, ?>) AttachmentUtils.get(pp).getComponentHandler())
					.getAttachmentService().getBean(pp.getParameter("id"));
		}
	}

	public static class AttachmentTooltipExPage extends AttachmentTooltipPage {

		@Override
		protected void onForward(final PageParameter pp) throws Exception {
			super.onForward(pp);

			final AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "AttachmentTooltipExPage_logPage",
					AttachmentLogPage.class);
			addWindowBean(pp, "AttachmentTooltipExPage_logWin", ajaxRequest).setHeight(480).setWidth(
					800);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Object getTopic(final PageParameter pp, final AttachmentFile attachment) {
			final ComponentParameter cp = AttachmentUtils.get(pp);
			return new LinkElement(super.getTopic(pp, attachment)).setOnclick(JS.loc(DownloadUtils
					.getDownloadHref(attachment, (Class<? extends IDownloadHandler>) cp
							.getComponentHandler().getClass())));
		}

		@Override
		protected Object getDownloads(final PageParameter pp, final AttachmentFile attachment) {
			final int downloads = attachment.getDownloads();
			if (downloads <= 0) {
				return 0;
			}
			final ComponentParameter cp = AttachmentUtils.get(pp);
			final StringBuilder sb = new StringBuilder();
			sb.append("$Actions['AttachmentTooltipExPage_logWin']('").append(AttachmentUtils.BEAN_ID)
					.append("=").append(cp.hashId()).append("&id=").append(attachment.getId())
					.append("');");
			return LinkButton.corner(downloads).setOnclick(sb.toString());
		}
	}
}
