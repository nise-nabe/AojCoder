package com.nisecoder.aojcoder;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AojCoderPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.nisecoder.aojcoder"; //$NON-NLS-1$

	// The shared instance
	private static AojCoderPlugin plugin;

	public CloseableHttpAsyncClient getHttpClient() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(3000).setConnectTimeout(3000).build();
		return HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig)
				.build();
	}

	/**
	 * The constructor
	 */
	public AojCoderPlugin() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AojCoderPlugin getDefault() {
		return plugin;
	}

}
