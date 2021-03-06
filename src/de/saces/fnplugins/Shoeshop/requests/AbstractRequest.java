package de.saces.fnplugins.Shoeshop.requests;

import com.db4o.ObjectContainer;

import freenet.client.async.ClientContext;
import freenet.client.events.ClientEvent;
import freenet.client.events.ClientEventListener;
import freenet.client.events.SplitfileProgressEvent;
import freenet.node.RequestClient;
import freenet.support.HTMLEncoder;
import freenet.support.api.Bucket;

public abstract class AbstractRequest<T> implements ClientEventListener, RequestClient {

	public enum STATUS { NONE, RUNNING, DONE, ERROR };
	public enum TYPE { FILE, SITE, INSERT };

	protected SplitfileProgressEvent _lastProgress;
	private final String _identifier;
	private Exception _lastError;

	private STATUS status = STATUS.NONE;
	private final TYPE _type;

	AbstractRequest(String identifier, TYPE type) {
		_identifier = identifier;
		_type = type;
	}

	public abstract void kill();
	public abstract void start(T param);
	public abstract Bucket getResult();

	@Override
	public void onRemoveEventProducer(ObjectContainer container) {
		new Exception("TODO").printStackTrace();
	}

	@Override
	public void receive(ClientEvent ce, ObjectContainer maybeContainer, ClientContext context) {
		if (ce instanceof SplitfileProgressEvent) {
			_lastProgress = (SplitfileProgressEvent) ce;
			return;
		}
		//System.out.println(ce);
		//new Exception("TODO: "+ce.getDescription()).printStackTrace();
	}

	@Override
	public boolean persistent() {
		return false;
	}

	@Override
	public void removeFrom(ObjectContainer container) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean realTimeFlag() {
		return true;
	}

	public void onMajorProgress(@SuppressWarnings("unused") ObjectContainer container) {
		// ignore
	}

	/**
	 * @return String The returning string is passed as is, so it can contain tags (&lt;br /&gt; for example) and you have to HTMLEncode yourself
	 */
	public String getRequestInfo() {
		StringBuilder sb = new StringBuilder();
		if (_lastProgress != null) {
			HTMLEncoder.encodeToBuffer(_lastProgress.getDescription(), sb);
		}
		return sb.toString();
	}

	void setStatusRunning() {
		if (status != STATUS.NONE) {
			if (status == STATUS.ERROR) {
				// timing/flow problem, ignore
				return;
			}
			new Exception("TODO").printStackTrace();
			return;
		}
		status = STATUS.RUNNING;
	}

	void setStatusError(Exception e) {
		_lastError = e;
		status = STATUS.ERROR;
	}

	void setStatusSuccess() {
		status = STATUS.DONE;
	}

	public String getStatus() {
		return status.name();
	}

	public boolean isRunning() {
		return status == STATUS.RUNNING;
	}

	public String getID() {
		return _identifier;
	}

	public String getType() {
		return _type.name();
	}

	public String getErrorInfo() {
		return _lastError.getLocalizedMessage();
	}

	public boolean isError() {
		return status == STATUS.ERROR;
	}

	public boolean isTypeInsert() {
		return _type == TYPE.INSERT;
	}

	public boolean isDone() {
		return status == STATUS.DONE;
	}

	public boolean isStarted() {
		return status != STATUS.NONE;
	}
}
