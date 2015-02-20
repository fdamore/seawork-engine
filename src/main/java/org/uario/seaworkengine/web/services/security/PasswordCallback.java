package org.uario.seaworkengine.web.services.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;

public class PasswordCallback implements CallbackHandler {

	@Override
	public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		final WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];

		if ("joe".equals(pc.getIdentifier())) {
			pc.setPassword("joespassword");
		}
	}
}